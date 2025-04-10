package kr.hhplus.be.server.infra.product;

import kr.hhplus.be.server.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {

    @Query("""
        SELECT p
        FROM Product p
        JOIN FETCH p.stock
    """)
    List<Product> findAllWithStock();
}
