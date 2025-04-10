package kr.hhplus.be.server.infra.order;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final EntityManager em;

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public Order findById(Long id) {
        return orderJpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
    }

    @Override
    public List<Object[]> findPopularProductIdsSince(LocalDateTime since, int limit) {
        return em.createQuery("""
                SELECT o.product.id, SUM(o.quantity)
                FROM Order o
                WHERE o.createdAt >= :since
                GROUP BY o.product.id
                ORDER BY SUM(o.quantity) DESC
                """, Object[].class)
                .setParameter("since", since)
                .setMaxResults(5)
                .getResultList();
    }

}
