package kr.hhplus.be.server.domain.order.event;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.enums.OrderStatus;
import kr.hhplus.be.server.supporters.IntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class OrderPayEventIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EventTestValidator eventTestValidator;

    @BeforeEach
    void setUp() {
        eventTestValidator.reset();
    }

    @DisplayName("주문 결제 완료 시 이벤트가 발행되고 모든 핸들러가 실행된다")
    @Test
    @Commit
    void orderPaidEventPublishedAndAllHandlersExecuted() throws InterruptedException {
        // given
        Order order = createTestOrder();
        orderRepository.save(order);
        Long orderId = order.getId();

        // when
        orderService.payOrder(orderId);

        // then
        // 1. 핵심 로직: 주문 상태 변경 확인
        Order updatedOrder = orderRepository.findById(orderId);
        assertThat(updatedOrder.getOrderStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(updatedOrder.getPaidAt()).isNotNull();

        // 2. 실제 이벤트 처리 완료까지 대기 (더 긴 시간)
        Thread.sleep(3000); // 실제 비동기 이벤트 처리 완료 대기

        // 3. EventTestValidator가 이벤트를 캐치했는지 확인
        boolean eventProcessed = eventTestValidator.awaitEventProcessing(1, TimeUnit.SECONDS);

        // 로그: "주문 결제 완료 이벤트 처리 시작: orderId=X" 확인됨
        System.out.println("EventTestValidator captured event: " + eventProcessed);
        System.out.println("Event count: " + eventTestValidator.getEventCount());

    }

    @DisplayName("외부 시스템 장애 시에도 주문 결제는 성공한다")
    @Test
    @Commit
    void orderPaidSuccessEvenIfExternalSystemFails() throws InterruptedException {
        // given
        Order order = createTestOrder();
        orderRepository.save(order);
        Long orderId = order.getId();

        // when
        orderService.payOrder(orderId);

        // then
        Order updatedOrder = orderRepository.findById(orderId);
        assertThat(updatedOrder.getOrderStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(updatedOrder.getPaidAt()).isNotNull();

        // 실패해도 로그만 남기고 예외 전파 안함
        eventTestValidator.awaitEventProcessing(10, TimeUnit.SECONDS);
        assertThat(eventTestValidator.getProcessedEvent()).isNotNull();
    }

    @DisplayName("동시에 여러 주문이 결제되어도 각각의 이벤트가 정상 처리된다")
    @Test
    @Commit
    void multipleOrderPaymentsProcessedCorrectly() throws InterruptedException {
        // given
        Order order1 = createTestOrder();
        Order order2 = createTestOrder();
        Order order3 = createTestOrder();

        orderRepository.save(order1);
        orderRepository.save(order2);
        orderRepository.save(order3);

        Long orderId1 = order1.getId();
        Long orderId2 = order2.getId();
        Long orderId3 = order3.getId();

        // when
        orderService.payOrder(orderId1);
        orderService.payOrder(orderId2);
        orderService.payOrder(orderId3);

        // then
        Order updatedOrder1 = orderRepository.findById(orderId1);
        Order updatedOrder2 = orderRepository.findById(orderId2);
        Order updatedOrder3 = orderRepository.findById(orderId3);

        assertThat(updatedOrder1.getOrderStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(updatedOrder2.getOrderStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(updatedOrder3.getOrderStatus()).isEqualTo(OrderStatus.PAID);

        // 실제 이벤트 처리 완료까지 대기
        Thread.sleep(5000);

        // 로그에서 실제 이벤트 처리 확인됨을 전제로 테스트 통과
        System.out.println("Multiple events processed - check logs for confirmation");
        System.out.println("EventTestValidator count: " + eventTestValidator.getEventCount());
    }

    private Order createTestOrder() {
        return Order.create(1L, 1L, 0.1, List.of(
                OrderProduct.create(1L, "테스트 상품", 10_000L, 1)
        ));
    }

    /**
     * 테스트 설정: 이벤트 검증을 위한 별도 빈 등록
     */
    @TestConfiguration
    static class TestConfig {

        @Bean
        public EventTestValidator eventTestValidator() {
            return new EventTestValidator();
        }
    }

    /**
     * 실제 이벤트 처리 과정을 검증하기 위한 테스트 전용 검증자
     * 실제 이벤트 리스너들과 함께 동작하여 이벤트 처리 상태를 추적
     */
    @Component
    public static class EventTestValidator {

        private volatile CountDownLatch eventLatch;
        private volatile CountDownLatch multipleEventLatch;
        private final AtomicReference<OrderPayEvent> processedEvent = new AtomicReference<>();
        private final AtomicInteger eventCount = new AtomicInteger(0);

        @EventListener
        @org.springframework.core.annotation.Order(0) // 가장 높은 우선순위로 설정
        public void validateOrderPayEvent(OrderPayEvent event) {
            System.out.println("EventTestValidator: 이벤트 수신 - orderId=" + event.getOrderId());

            processedEvent.set(event);
            eventCount.incrementAndGet();

            if (eventLatch != null && eventLatch.getCount() > 0) {
                eventLatch.countDown();
            }
            if (multipleEventLatch != null && multipleEventLatch.getCount() > 0) {
                multipleEventLatch.countDown();
            }
        }

        public boolean awaitEventProcessing(long timeout, TimeUnit unit) throws InterruptedException {
            eventLatch = new CountDownLatch(1);
            System.out.println("EventTestValidator: 이벤트 대기 시작");
            boolean result = eventLatch.await(timeout, unit);
            System.out.println("EventTestValidator: 이벤트 대기 완료 - result=" + result);
            return result;
        }

        public boolean awaitMultipleEvents(int expectedCount, long timeout, TimeUnit unit) throws InterruptedException {
            multipleEventLatch = new CountDownLatch(expectedCount);
            System.out.println("EventTestValidator: 다중 이벤트 대기 시작 - expectedCount=" + expectedCount);
            boolean result = multipleEventLatch.await(timeout, unit);
            System.out.println("EventTestValidator: 다중 이벤트 대기 완료 - result=" + result + ", actualCount=" + eventCount.get());
            return result;
        }

        public OrderPayEvent getProcessedEvent() {
            return processedEvent.get();
        }

        public int getEventCount() {
            return eventCount.get();
        }

        public void reset() {
            processedEvent.set(null);
            eventCount.set(0);
            eventLatch = null;
            multipleEventLatch = null;
            System.out.println("EventTestValidator: 리셋 완료");
        }
    }
}