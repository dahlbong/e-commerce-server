package kr.hhplus.be.server.domain.order;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository {
    Order save(Order order);
    Order findById(Long id);
    List<OrderProduct> findOrderIdsIn(List<Long> orderIds);
    List<OrderInfo.PaidProduct> findPaidProducts(OrderCommand.PaidProducts command);
}
