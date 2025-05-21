package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.domain.order.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderProductJpaRepository extends JpaRepository<OrderProduct, Long> {

    List<OrderProduct> findByOrderIdIn(List<Long> orderIds);
}
