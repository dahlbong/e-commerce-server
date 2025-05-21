package kr.hhplus.be.server.infra.point;

import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.point.PointTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepository {

    private final PointJpaRepository jpaRepository;
    private final PointTransactionJpaRepository transactionJpaRepository;

    @Override
    public Optional<Point> findOptionalByUserId(Long userId) {
        return jpaRepository.findByUserId(userId);
    }

    @Override
    public Point save(Point point) {
        return jpaRepository.save(point);
    }

    @Override
    public PointTransaction saveTransaction(PointTransaction transaction) {
        return transactionJpaRepository.save(transaction);
    }
}
