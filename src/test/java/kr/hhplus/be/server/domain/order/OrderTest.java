package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.BusinessException;
import kr.hhplus.be.server.domain.order.enums.OrderErrorCode;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.enums.SellingStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class OrderTest {

    private Product createProduct(String name, BigDecimal price) {
        return Product.of(name, SellingStatus.SELLING, price, 50);
    }

    @Test
    @DisplayName("총 결제 금액은 수량 * 단가 - 할인금액으로 계산된다")
    void calculateTotalPrice_success() {
        Product product = createProduct("상품A", BigDecimal.valueOf(10000));
        Order order = Order.of(1L, product, 2);
        order.applyDiscount(BigDecimal.valueOf(2000));

        assertThat(order.calculateTotalPrice()).isEqualByComparingTo("18000");
    }

    @Test
    @DisplayName("0 이하의 수량은 QUANTITY_SHOULD_BE_POSITIVE 예외를 던진다")
    void zero_quantity_should_throw() {
        Product product = createProduct("상품A", BigDecimal.valueOf(1000));

        assertThatThrownBy(() -> Order.of(1L, product, 0))
                .isInstanceOf(BusinessException.class)
                .hasMessage(OrderErrorCode.QUANTITY_SHOULD_BE_POSITIVE.message());
    }
}
