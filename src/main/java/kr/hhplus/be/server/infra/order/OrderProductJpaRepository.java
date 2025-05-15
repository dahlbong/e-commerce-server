package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.domain.order.OrderProduct;

import java.util.List;

public interface OrderProductRepository {

    List<OrderProduct> findByOrderIdIn(List<Long> orderIds);
}
