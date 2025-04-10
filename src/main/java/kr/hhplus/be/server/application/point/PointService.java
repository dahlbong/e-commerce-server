package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.PointHistoryRepository;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.point.enums.PointHistoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    public Point charge(Long userId, BigDecimal amount) {
        Point point = pointRepository.findByUserId(userId);
        BigDecimal before = point.getBalance();

        point.charge(amount);
        pointRepository.save(point);

        PointHistory history = PointHistory.of(userId, PointHistoryType.CHARGE, amount, before, point.getBalance());
        pointHistoryRepository.save(history);

        return point;
    }

    @Transactional
    public Point use(Long userId, BigDecimal amount) {
        Point point = pointRepository.findByUserId(userId);
        BigDecimal before = point.getBalance();

        point.use(amount);
        pointRepository.save(point);

        PointHistory history = PointHistory.of(
                userId,
                PointHistoryType.USE,
                amount,
                before,
                point.getBalance()
        );
        pointHistoryRepository.save(history);

        return point;
    }

    public Point get(Long userId) {
        return pointRepository.findByUserId(userId);
    }

}