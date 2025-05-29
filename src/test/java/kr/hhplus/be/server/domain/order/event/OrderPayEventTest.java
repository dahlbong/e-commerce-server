package kr.hhplus.be.server.domain.order.event;

import kr.hhplus.be.server.supporters.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderPayEventTest extends MockTestSupport {

    @DisplayName("OrderPayEvent를 생성한다.")
    @Test
    void createOrderPayEventWithBuilder() {
        // given
        Long orderId = 1L;
        Long userId = 2L;
        long totalPrice = 10_000L;
        long discountPrice = 1_000L;

        // when
        OrderPayEvent orderEvent = OrderPayEvent.builder()
                .orderId(orderId)
                .userId(userId)
                .totalPrice(totalPrice)
                .discountPrice(discountPrice)
                .build();

        // then
        assertThat(orderEvent.getOrderId()).isEqualTo(orderId);
        assertThat(orderEvent.getUserId()).isEqualTo(userId);
        assertThat(orderEvent.getTotalPrice()).isEqualTo(totalPrice);
        assertThat(orderEvent.getDiscountPrice()).isEqualTo(discountPrice);
    }

    @DisplayName("OrderPayEvent 빌더에서 일부 필드만 설정할 수 있다.")
    @Test
    void createOrderPayEventWithPartialFields() {
        // given & when
        OrderPayEvent orderEvent = OrderPayEvent.builder()
                .orderId(1L)
                .userId(2L)
                .build();

        // then
        assertThat(orderEvent.getOrderId()).isEqualTo(1L);
        assertThat(orderEvent.getUserId()).isEqualTo(2L);
        assertThat(orderEvent.getTotalPrice()).isEqualTo(0L);
        assertThat(orderEvent.getDiscountPrice()).isEqualTo(0L);
    }

}