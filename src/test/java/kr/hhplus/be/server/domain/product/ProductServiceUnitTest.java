package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.enums.SellingStatus;
import kr.hhplus.be.server.supporters.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductServiceUnitTest extends MockTestSupport {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @DisplayName("유효한 ID로 주문 상품 목록을 가져와야 한다.")
    @Test
    void getOrderProductsWithInvalidProductId() {
        // given
        ProductCommand.OrderProducts command = mock(ProductCommand.OrderProducts.class);
        ProductCommand.OrderProduct orderProduct = mock(ProductCommand.OrderProduct.class);

        when(command.getProducts())
            .thenReturn(List.of(orderProduct, orderProduct));

        when(productRepository.findById(anyLong()))
            .thenThrow(new IllegalArgumentException("상품이 존재하지 않습니다."));

        // when & then
        assertThatThrownBy(() -> productService.getOrderProducts(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("상품이 존재하지 않습니다.");
    }

    @DisplayName("주문 가능한 상품 목록을 가져와야 한다.")
    @Test
    void getOrderProductsIncludeNonSellingProduct() {
        // given
        ProductCommand.OrderProducts command = mock(ProductCommand.OrderProducts.class);
        ProductCommand.OrderProduct orderProduct = mock(ProductCommand.OrderProduct.class);

        when(command.getProducts())
            .thenReturn(List.of(orderProduct, orderProduct));

        when(productRepository.findById(anyLong()))
            .thenReturn(Product.builder()
                .sellStatus(SellingStatus.STOP_SELLING)
                .build());

        // when & then
        assertThatThrownBy(() -> productService.getOrderProducts(command))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("주문 불가한 상품이 포함되어 있습니다.");
    }

    @DisplayName("주문 상품 목록을 가져온다.")
    @Test
    void getOrderProducts() {
        // given
        ProductCommand.OrderProducts command = mock(ProductCommand.OrderProducts.class);
        ProductCommand.OrderProduct orderProduct = mock(ProductCommand.OrderProduct.class);

        when(orderProduct.getQuantity())
            .thenReturn(1);

        when(command.getProducts())
            .thenReturn(List.of(orderProduct, orderProduct));

        when(productRepository.findById(anyLong()))
            .thenReturn(Product.builder()
                .name("상품명")
                .price(10_000L)
                .sellStatus(SellingStatus.SELLING)
                .build());

        // when
        ProductInfo.OrderProducts orderProducts = productService.getOrderProducts(command);

        // then
        assertThat(orderProducts.getProducts()).hasSize(2)
            .extracting("productName", "productPrice", "quantity")
            .containsExactly(
                tuple("상품명", 10_000L, 1),
                tuple("상품명", 10_000L, 1)
            );
    }

    @DisplayName("판매 가능한 상품을 조회한다.")
    @Test
    void getSellingProducts() {
        // given
        List<Product> sellingProducts = List.of(
            Product.create("상품명1", 10_000L, SellingStatus.SELLING),
            Product.create("상품명3", 15_000L, SellingStatus.SELLING),
            Product.create("상품명5", 30_000L, SellingStatus.SELLING),
            Product.create("상품명7", 18_000L, SellingStatus.SELLING),
            Product.create("상품명9", 35_000L, SellingStatus.SELLING)
        );

        when(productRepository.findSellingStatusIn(anyList()))
            .thenReturn(sellingProducts);

        // when
        ProductInfo.Products products = productService.getSellingProducts();

        // then
        assertThat(products.getProducts()).hasSize(5)
            .extracting("productName", "productPrice")
            .containsExactly(
                tuple("상품명1", 10_000L),
                tuple("상품명3", 15_000L),
                tuple("상품명5", 30_000L),
                tuple("상품명7", 18_000L),
                tuple("상품명9", 35_000L)
            );
    }
}