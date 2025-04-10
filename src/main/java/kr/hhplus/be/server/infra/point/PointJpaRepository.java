package kr.hhplus.be.server.infra.point;

import kr.hhplus.be.server.domain.point.Point;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointJpaRepository extends JpaRepository<Point, Long> {
    Point findByUserId(Long userId);
}