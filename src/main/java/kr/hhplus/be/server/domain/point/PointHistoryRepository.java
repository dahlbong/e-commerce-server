package kr.hhplus.be.server.domain.point;

import java.util.List;

public interface PointHistoryRepository {
    void save(PointHistory history);
    List<PointHistory> findAllByUserId(Long userId);
}