package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.BusinessException;
import kr.hhplus.be.server.domain.product.PopularProduct;
import kr.hhplus.be.server.domain.product.PopularProductRepository;
import kr.hhplus.be.server.domain.product.enums.ProductErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PopularProductServiceTest {

    @Mock
    PopularProductRepository popularProductRepository;

    @InjectMocks
    PopularProductService popularProductService;

    List<PopularProduct> mockProducts;

    @BeforeEach
    void setup() {
        mockProducts = List.of(
                PopularProduct.of(1L, "콜라", BigDecimal.valueOf(1500), 50),
                PopularProduct.of(2L, "사이다", BigDecimal.valueOf(1400), 30)
        );
    }

    @Test
    @DisplayName("인기 상품을 정상적으로 저장할 수 있다")
    void update_should_save_popular_products() {
        // when
        popularProductService.update(mockProducts);

        // then
        verify(popularProductRepository).saveAll(mockProducts);
    }

    @Test
    @DisplayName("빈 리스트를 저장하려 할 경우 saveAll을 호출하지 않는다")
    void update_should_ignore_empty_input() {
        // when
        popularProductService.update(Collections.emptyList());

        // then
        verify(popularProductRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("인기 상품 상위 5개를 정상적으로 조회할 수 있다")
    void getTop5_should_return_products() {
        // given
        given(popularProductRepository.findTop5ByOrderByTotalOrderCountDesc()).willReturn(mockProducts);

        // when
        List<PopularProduct> result = popularProductService.getTop5();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("콜라");
        verify(popularProductRepository).findTop5ByOrderByTotalOrderCountDesc();
    }

    @Test
    @DisplayName("인기 상품 조회 결과가 없을 경우 빈 리스트를 반환한다")
    void getTop5_should_return_empty_if_none() {
        // given
        given(popularProductRepository.findTop5ByOrderByTotalOrderCountDesc()).willReturn(Collections.emptyList());

        // when
        List<PopularProduct> result = popularProductService.getTop5();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("인기 상품 저장 중 repository에서 예외 발생 시 BusinessException으로 감싼다")
    void update_should_throw_exception_on_save_failure() {
        // given
        willThrow(new RuntimeException("DB 오류")).given(popularProductRepository).saveAll(any());

        // when & then
        assertThatThrownBy(() -> popularProductService.update(mockProducts))
                .hasMessage(ProductErrorCode.POPULAR_PRODUCT_UPDATE_FAILED.message());
    }
}
