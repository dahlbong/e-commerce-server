package kr.hhplus.be.server.infra.point;

import kr.hhplus.be.server.domain.point.PointTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointTransactionJpaRepository extends JpaRepository<PointTransaction, Long> {
}
