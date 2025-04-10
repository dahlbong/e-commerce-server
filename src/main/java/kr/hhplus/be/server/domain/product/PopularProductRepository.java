package kr.hhplus.be.server.domain.product;

import java.util.List;

public interface PopularProductRepository {
    void saveAll(List<PopularProduct> products);
    List<PopularProduct> findTop5ByOrderByTotalOrderCountDesc();
}
