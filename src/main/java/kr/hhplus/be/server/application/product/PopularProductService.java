package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.BusinessException;
import kr.hhplus.be.server.domain.product.PopularProduct;
import kr.hhplus.be.server.domain.product.PopularProductRepository;
import kr.hhplus.be.server.domain.product.enums.ProductErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PopularProductService {

    private final PopularProductRepository popularProductRepository;

    public void update(List<PopularProduct> products) {
        if (products == null || products.isEmpty()) return;

        try {
            popularProductRepository.saveAll(products);
        } catch (Exception e) {
            throw new BusinessException(ProductErrorCode.POPULAR_PRODUCT_UPDATE_FAILED);
        }
    }

    public List<PopularProduct> getTop5() {
        return popularProductRepository.findTop5ByOrderByTotalOrderCountDesc();
    }
}
