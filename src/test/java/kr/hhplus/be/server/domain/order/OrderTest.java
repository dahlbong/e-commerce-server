package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.enums.OrderErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class OrderTest {

    @Test
    @DisplayName("총 결제 금액은 수량 * 단가 - 할인금액으로 계산된다")
    void calculateTotalPrice_success() {
        Order order = Order.of(1L, 1L, BigDecimal.valueOf(10000), 2);
        order.applyDiscount(BigDecimal.valueOf(2000));
        assertThat(order.calculateTotalPrice()).isEqualByComparingTo("18000");
    }

    @Test
    @DisplayName("0 이하의 수량은 QUANTITY_SHOULD_BE_POSITIVE 예외를 던진다")
    void zero_quantity_should_throw() {
        assertThatThrownBy(() ->
                Order.of(1L, 1L, BigDecimal.valueOf(1000), 0))
                .hasMessage(OrderErrorCode.QUANTITY_SHOULD_BE_POSITIVE.message());
    }
}
