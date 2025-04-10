package kr.hhplus.be.server.infra.point;

import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepository {

    private final PointJpaRepository jpaRepository;

    @Override
    public Point findByUserId(Long userId) {
        Point point = jpaRepository.findByUserId(userId);
        if (point == null) {
            // 포인트가 없으면 0원으로 새로 생성
            point = Point.of(userId, BigDecimal.ZERO);
            jpaRepository.save(point); // 영속화까지 할지 말지는 전략적으로 선택
        }
        return point;
    }
    @Override
    public Point save(Point point) {
        return jpaRepository.save(point);
    }
}
