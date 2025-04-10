package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.BusinessException;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.enums.ProductErrorCode;
import kr.hhplus.be.server.domain.product.enums.SellingStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductService productService;

    @Test
    @DisplayName("모든 상품 목록을 조회할 수 있다")
    void getAllWithStock_returns_products() {
        // given
        Product p1 = Product.of("콜라", SellingStatus.SELLING, BigDecimal.valueOf(1500), 1);
        Product p2 = Product.of("사이다", SellingStatus.SELLING, BigDecimal.valueOf(1400), 1);

        given(productRepository.findAllWithStock()).willReturn(List.of(p1, p2));

        // when
        List<Product> result = productService.getAllWithStock();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Product::getName).containsExactly("콜라", "사이다");

        verify(productRepository, times(1)).findAllWithStock();
    }

    @Test
    @DisplayName("상품 ID로 상품을 조회할 수 있다")
    void getById_returns_product() {
        // given
        Product product = Product.of("물", SellingStatus.SELLING, BigDecimal.valueOf(1000), 1);
        given(productRepository.findById(1L)).willReturn(product);

        // when
        Product found = productService.getById(1L);

        // then
        assertThat(found.getName()).isEqualTo("물");
        assertThat(found.getPrice()).isEqualByComparingTo("1000");

        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("상품 ID로 상품을 조회할 때, 해당 상품이 없으면 PRODUCT_NOT_FOUND 예외가 발생한다")
    void getById_throws_exception_if_product_not_found() {
        // given
        given(productRepository.findById(999L)).willReturn(null); // null 반환

        // when & then
        assertThatThrownBy(() -> productService.getById(999L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ProductErrorCode.PRODUCT_NOT_FOUND.message());

        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("상품 목록 조회 시, 상품이 없으면 빈 리스트를 반환한다")
    void getAllWithStock_returns_empty_if_no_products() {
        // given
        given(productRepository.findAllWithStock()).willReturn(List.of());

        // when
        List<Product> result = productService.getAllWithStock();

        // then
        assertThat(result).isEmpty();

        verify(productRepository, times(1)).findAllWithStock();
    }

    @Test
    @DisplayName("상품 조회 시 재고 수량이 정확히 반환된다.")
    void getById_returns_correct_stock_quantity() {
        // given
        Product product = Product.of("물", SellingStatus.SELLING, BigDecimal.valueOf(1000), 50);

        given(productRepository.findById(1L)).willReturn(product);

        // when
        Product found = productService.getById(1L);

        // then
        assertThat(found.getRemainQuantity()).isEqualTo(50);

        verify(productRepository, times(1)).findById(1L);
    }
}
