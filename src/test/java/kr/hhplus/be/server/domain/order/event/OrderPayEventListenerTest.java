package kr.hhplus.be.server.domain.order.event;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderExternalClient;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.supporters.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderPayEventListenerTest extends MockTestSupport {

    @InjectMocks
    private OrderPayEventListener orderPayEventListener;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderExternalClient orderExternalClient;

    @DisplayName("주문 결제 이벤트를 처리하여 외부 클라이언트로 전송한다.")
    @Test
    void handleOrderPayEvent() {
        // given
        Order order = Order.create(1L, 1L, 0.1, List.of(
                OrderProduct.create(1L, "상품명", 10_000L, 2)
        ));

        OrderPayEvent event = OrderPayEvent.builder()
                .orderId(1L)
                .userId(1L)
                .totalPrice(18_000L)
                .discountPrice(2_000L)
                .build();

        when(orderRepository.findById(1L)).thenReturn(order);

        // when
        orderPayEventListener.handleOrderPayEvent(event);

        // then
        verify(orderRepository, times(1)).findById(1L);
        verify(orderExternalClient, times(1)).sendOrderMessage(order);
    }

    @DisplayName("존재하지 않는 주문에 대한 이벤트 처리 시 예외가 발생해도 로그만 출력하고 정상 종료된다")
    @Test
    void handleOrderPayEventWithNonExistentOrder() {
        // given
        OrderPayEvent event = OrderPayEvent.builder()
                .orderId(999L)
                .userId(1L)
                .totalPrice(18_000L)
                .discountPrice(2_000L)
                .build();

        when(orderRepository.findById(999L))
                .thenThrow(new IllegalArgumentException("주문이 존재하지 않습니다."));

        // when - 예외가 발생하지만 잡혀서 처리되므로 정상적으로 실행됨
        assertThatCode(() -> orderPayEventListener.handleOrderPayEvent(event))
                .doesNotThrowAnyException();

        // then
        verify(orderRepository, times(1)).findById(999L);
        verify(orderExternalClient, never()).sendOrderMessage(any());
    }

    @DisplayName("외부 클라이언트 전송 실패 시에도 예외가 전파되지 않는다.")
    @Test
    void handleOrderPayEventWithExternalClientFailure() {
        // given
        Order order = Order.create(1L, 1L, 0.1, List.of(
                OrderProduct.create(1L, "상품명", 10_000L, 2)
        ));

        OrderPayEvent event = OrderPayEvent.builder()
                .orderId(1L)
                .userId(1L)
                .totalPrice(18_000L)
                .discountPrice(2_000L)
                .build();

        when(orderRepository.findById(1L)).thenReturn(order);
        doThrow(new RuntimeException("External API Error"))
                .when(orderExternalClient).sendOrderMessage(order);

        // when & then
        // 예외가 발생하지 않아야 함 (이벤트 리스너에서 예외 처리)
        orderPayEventListener.handleOrderPayEvent(event);

        verify(orderRepository, times(1)).findById(1L);
        verify(orderExternalClient, times(1)).sendOrderMessage(order);
    }
}