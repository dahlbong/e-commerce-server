package kr.hhplus.be.server.infra.scheduler;

import kr.hhplus.be.server.domain.product.PopularProduct;
import kr.hhplus.be.server.domain.product.PopularProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PopularProductRepositoryImpl implements PopularProductRepository {

    private final PopularProductJpaRepository jpaRepository;

    @Override
    public void saveAll(List<PopularProduct> products) {
        jpaRepository.saveAll(products);
    }

    @Override
    public List<PopularProduct> findTop5ByOrderByTotalOrderCountDesc() {
        return jpaRepository.findTop5ByOrderByTotalOrderCountDesc();
    }
}
