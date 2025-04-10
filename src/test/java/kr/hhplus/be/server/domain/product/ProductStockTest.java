package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.enums.ProductErrorCode;
import kr.hhplus.be.server.domain.product.enums.SellingStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class ProductStockTest {

    Product PRODUCT = Product.of("testProduct", SellingStatus.SELLING, BigDecimal.valueOf(5000), 100);

    @Test
    @DisplayName("재고가 충분할 경우 정상적으로 감소된다")
    void decreaseStock_success() {

        PRODUCT.getStock().decrease(10);
        assertThat(PRODUCT.getStock()).isEqualTo(90);
    }

    @Test
    @DisplayName("재고보다 많은 수량을 차감하면 OUT_OF_STOCK 예외메시지를 던진다")
    void decreaseStock_fail_insufficient() {
        ProductStock stock = ProductStock.of(PRODUCT, 10);
        assertThatThrownBy(() -> stock.decrease(20))
                .hasMessage(ProductErrorCode.OUT_OF_STOCK.message());;
    }

    @Test
    @DisplayName("감소 수량이 0 이하일 경우 DECREASE_AMOUNT_SHOULD_BE_POSITIVE 예외메시지를 던진다")
    void decreaseStock_fail_invalidAmount() {
        ProductStock stock = ProductStock.of(PRODUCT, 10);
        assertThatThrownBy(() -> stock.decrease(0))
                .hasMessage(ProductErrorCode.DECREASE_AMOUNT_SHOULD_BE_POSITIVE.message());;
    }
}
