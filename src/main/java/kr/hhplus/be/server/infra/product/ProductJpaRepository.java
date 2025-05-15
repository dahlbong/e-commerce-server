package kr.hhplus.be.server.infra.product;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.enums.SellingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {


    List<Product> findBySellStatusIn(List<SellingStatus> sellStatuses);

    List<Product> findByIdIn(List<Long> ids);
}