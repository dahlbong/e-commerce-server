package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.enums.ProductErrorCode;
import kr.hhplus.be.server.domain.product.enums.SellingStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class ProductTest {

    @Test
    @DisplayName("상품이 정상적으로 생성된다")
    void createProduct_success() {
        Product product = Product.of(
                1L,
                "올리브영 토너",
                SellingStatus.SELLING,
                BigDecimal.valueOf(8900)
        );

        assertThat(product.getName()).isEqualTo("올리브영 토너");
        assertThat(product.getPrice()).isEqualByComparingTo("8900");
        assertThat(product.getSellingStatus()).isEqualTo(SellingStatus.SELLING);
        assertThat(product.getCreatedAt()).isNotNull();
        assertThat(product.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("상품 이름이 비어 있으면 NAME_SHOULD_NOT_BE_BLANK 예외메시지를 던진다.")
    void createProduct_fail_blankName() {
        assertThatThrownBy(() ->
                Product.of(1L, "  ", SellingStatus.SELLING, BigDecimal.valueOf(10000)))
                .hasMessage(ProductErrorCode.NAME_SHOULD_NOT_BE_BLANK.message());
    }

    @Test
    @DisplayName("상품 가격이 0 이하이면 PRICE_SHOULD_BE_POSITIVE 예외메시지를 던진다.")
    void createProduct_fail_invalidPrice() {
        assertThatThrownBy(() ->
                Product.of(1L, "상품", SellingStatus.SELLING, BigDecimal.ZERO))
                .hasMessage(ProductErrorCode.PRICE_SHOULD_BE_POSITIVE.message());
    }

    @Test
    @DisplayName("판매 상태가 SELLING이면 isSelling은 true를 반환한다")
    void isSelling_returns_true_when_selling() {
        Product product = Product.of(1L, "상품", SellingStatus.SELLING, BigDecimal.valueOf(10000));
        assertThat(product.isSelling()).isTrue();
    }

    @Test
    @DisplayName("판매 상태가 STOPPED이면 isSelling은 false를 반환한다")
    void isSelling_returns_false_when_stopped() {
        Product product = Product.of(1L, "상품", SellingStatus.STOPPED, BigDecimal.valueOf(10000));
        assertThat(product.isSelling()).isFalse();
    }
}
