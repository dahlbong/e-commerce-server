package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.enums.OrderStatus;
import kr.hhplus.be.server.domain.order.event.OrderPayEvent;
import kr.hhplus.be.server.domain.order.event.OrderPayEventPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceUnitTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderPayEventPublisher orderPayEventPublisher;

    @InjectMocks
    private OrderService orderService;

    @DisplayName("주문을 결제한다. 결제 완료 시, 외부 데이터 플랫폼으로 주문정보를 전송한다.")
    @Test
    void payOrder() {
        // given
        Order order = Order.create(1L,
                1L,
                0.1,
                List.of(
                        OrderProduct.create(1L, "상품명", 2_000L, 2)
                )
        );

        setOrderId(order, 100L);

        when(orderRepository.findById(any()))
                .thenReturn(order);

        // when
        orderService.payOrder(1L);

        // then
        // 1. 주문 상태가 PAID로 변경되었는지 확인
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(order.getPaidAt()).isNotNull();

        // 2. 이벤트가 발행되었는지 확인
        verify(orderPayEventPublisher, times(1)).publishOrderPayEvent(any(OrderPayEvent.class));

        // 3. 발행된 이벤트의 내용 검증 (ArgumentCaptor 사용)
        ArgumentCaptor<OrderPayEvent> eventCaptor = ArgumentCaptor.forClass(OrderPayEvent.class);
        verify(orderPayEventPublisher).publishOrderPayEvent(eventCaptor.capture());

        OrderPayEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getOrderId()).isEqualTo(100L);
        assertThat(capturedEvent.getUserId()).isEqualTo(1L);
        assertThat(capturedEvent.getTotalPrice()).isEqualTo(order.getTotalPrice());
        assertThat(capturedEvent.getDiscountPrice()).isEqualTo(order.getDiscountPrice());
    }

    @DisplayName("주문을 생성한다.")
    @Test
    void createOrder() {
        // given
        OrderCommand.Create command = OrderCommand.Create.builder()
                .userId(1L)
                .userCouponId(1L)
                .discountRate(0.1)
                .products(List.of(
                        OrderCommand.OrderProduct.builder()
                                .productId(1L)
                                .productName("상품명")
                                .productPrice(2_000L)
                                .quantity(2)
                                .build()
                ))
                .build();

        Order savedOrder = Order.create(
                command.getUserId(),
                command.getUserCouponId(),
                command.getDiscountRate(),
                List.of(OrderProduct.create(1L, "상품명", 2_000L, 2))
        );

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // when
        OrderInfo.Order result = orderService.createOrder(command);

        // then
        assertThat(result.getOrderId()).isEqualTo(savedOrder.getId());
        assertThat(result.getTotalPrice()).isEqualTo(savedOrder.getTotalPrice());
        assertThat(result.getDiscountPrice()).isEqualTo(savedOrder.getDiscountPrice());

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @DisplayName("특정 날짜의 결제된 상품을 조회한다.")
    @Test
    void getPaidProducts() {
        // given
        OrderCommand.DateQuery dateQuery = OrderCommand.DateQuery.of(
                java.time.LocalDate.of(2024, 1, 1)
        );

        List<OrderInfo.PaidProduct> mockPaidProducts = List.of(
                OrderInfo.PaidProduct.of(1L, 2),
                OrderInfo.PaidProduct.of(2L, 1)
        );

        when(orderRepository.findPaidProducts(any(OrderCommand.PaidProducts.class)))
                .thenReturn(mockPaidProducts);

        // when
        OrderInfo.PaidProducts result = orderService.getPaidProducts(dateQuery);

        // then
        assertThat(result.getProducts()).hasSize(2);
        assertThat(result.getProducts()).extracting("productId")
                .containsExactly(1L, 2L);
        assertThat(result.getProducts()).extracting("quantity")
                .containsExactly(2, 1);

        verify(orderRepository, times(1)).findPaidProducts(any(OrderCommand.PaidProducts.class));
    }

    private void setOrderId(Order order, Long id) {
        try {
            Field idField = Order.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(order, id);
        } catch (Exception e) {
            throw new RuntimeException("Order id 설정 실패", e);
        }
    }
}