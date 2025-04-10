package kr.hhplus.be.server.domain.order;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository {
    Order save(Order order);
    Order findById(Long id);
    List<Object[]> findPopularProductIdsSince(LocalDateTime since, int limit);
}
