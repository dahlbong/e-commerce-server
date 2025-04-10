package kr.hhplus.be.server.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ProductStockTest {

    @Test
    @DisplayName("재고가 충분할 경우 정상적으로 감소된다")
    void decreaseStock_success() {
        ProductStock stock = ProductStock.of(1L, 10L, 100);
        stock.decrease(10);
        assertThat(stock.getQuantity()).isEqualTo(90);
    }

    @Test
    @DisplayName("재고보다 많은 수량을 차감하면 OUT_OF_STOCK 예외메시지를 던진다")
    void decreaseStock_fail_insufficient() {
        ProductStock stock = ProductStock.of(1L, 10L, 10);
        assertThatThrownBy(() -> stock.decrease(20))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ProductErrorCode.OUT_OF_STOCK.message());;
    }

    @Test
    @DisplayName("감소 수량이 0 이하일 경우 DECREASE_AMOUNT_SHOULD_BE_POSITIVE 예외메시지를 던진다")
    void decreaseStock_fail_invalidAmount() {
        ProductStock stock = ProductStock.of(1L, 10L, 10);
        assertThatThrownBy(() -> stock.decrease(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ProductErrorCode.DECREASE_AMOUNT_SHOULD_BE_POSITIVE.message());;
    }
}
