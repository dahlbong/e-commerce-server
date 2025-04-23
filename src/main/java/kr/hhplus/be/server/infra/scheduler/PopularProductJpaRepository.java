package kr.hhplus.be.server.infra.scheduler;

import kr.hhplus.be.server.domain.product.PopularProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PopularProductJpaRepository extends JpaRepository<PopularProduct, Long> {
    List<PopularProduct> findTop5ByOrderByTotalOrderCountDesc();
}
