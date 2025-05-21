package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.enums.SellingStatus;

import java.util.List;

public interface ProductRepository {
    Product save(Product product);

    Product findById(Long productId);

    List<Product> findSellingStatusIn(List<SellingStatus> statuses);
}
