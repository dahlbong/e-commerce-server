package kr.hhplus.be.server.infra.point;

import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PointHistoryRepositoryImpl implements PointHistoryRepository {

    private final PointHistoryJpaRepository jpaRepository;

    @Override
    public void save(PointHistory history) {
        jpaRepository.save(history);
    }

    @Override
    public List<PointHistory> findAllByUserId(Long userId) {
        return List.of();
    }
}
