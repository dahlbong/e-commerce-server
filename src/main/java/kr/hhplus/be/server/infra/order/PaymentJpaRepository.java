package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.domain.order.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
}
