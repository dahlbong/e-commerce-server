package kr.hhplus.be.server.domain.order.event;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderExternalClient;
import kr.hhplus.be.server.domain.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPayEventListener {

    private final OrderExternalClient orderExternalClient;
    private final OrderRepository orderRepository;

    /**
     * 주문 결제 완료 후 외부 데이터 플랫폼으로 주문 정보 전송
     * - 트랜잭션 커밋 후 비동기로 실행
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderEvent(OrderPayEvent event) {
        try {
            log.info("주문 결제 완료 이벤트 처리 시작: orderId={}", event.getOrderId());

            Order order = orderRepository.findById(event.getOrderId());
            orderExternalClient.sendOrderMessage(order);

            log.info("외부 데이터 플랫폼 주문 정보 전송 완료: orderId={}", event.getOrderId());

        } catch (Exception e) {
            log.error("외부 데이터 플랫폼 주문 정보 전송 실패: orderId={}, error={}",
                    event.getOrderId(), e.getMessage(), e);
            // 부가 로직 실패는 핵심 로직에 영향을 주지 않도록 예외를 외부로 전파하지 않음
        }
    }

    /**
     * 결제 완료 알림톡 발송 확장 고려
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderEventForNotification(OrderPayEvent event) {
        try {
            log.info("결제 완료 알림톡 발송 시작: orderId={}", event.getOrderId());
            log.info("결제 완료 알림톡 발송 완료: orderId={}, userId={}, amount={}",
                    event.getOrderId(), event.getUserId(), event.getTotalPrice());

        } catch (Exception e) {
            log.error("결제 완료 알림톡 발송 실패: orderId={}, error={}",
                    event.getOrderId(), e.getMessage(), e);
        }
    }
}