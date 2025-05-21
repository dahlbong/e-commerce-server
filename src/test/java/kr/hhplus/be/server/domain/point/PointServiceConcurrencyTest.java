package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.supporters.ConcurrencyTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class PointServiceConcurrencyTest extends ConcurrencyTestSupport {

    @Autowired
    private PointService pointService;

    @Autowired
    private PointRepository pointRepository;

    @DisplayName("잔액 충전 시, 동시에 충전 요청이 들어오면 하나만 성공해야 한다.")
    @Test
    void chargePointWithOptimisticLock() {
        // given
        Long userId = 1L;
        Point point = Point.create(userId);
        pointRepository.save(point);

        PointCommand.Charge command = PointCommand.Charge.of(userId, 1_000L);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        executeConcurrency(2, () -> {
            try {
                pointService.chargePoint(command);
                successCount.incrementAndGet();
            } catch (Exception e) {
                failCount.incrementAndGet();
            }
        });

        // then
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);

        Point chargedPoint = pointRepository.findOptionalByUserId(userId).orElseThrow();
        assertThat(chargedPoint.getAmount()).isEqualTo(1_000L);
    }

    @DisplayName("잔액 사용 시, 동시에 사용 요청이 들어오면 하나만 성공해야 한다.")
    @Test
    void usePointWithOptimisticLock() {
        // given
        Long userId = 1L;
        Point point = Point.create(userId);
        point.charge(1_000L);
        pointRepository.save(point);

        PointCommand.Use command = PointCommand.Use.of(userId, 500L);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        executeConcurrency(2, () -> {
            try {
                pointService.usePoint(command);
                successCount.incrementAndGet();
            } catch (Exception e) {
                failCount.incrementAndGet();
            }
        });

        // then
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);

        Point usedPoint = pointRepository.findOptionalByUserId(userId).orElseThrow();
        assertThat(usedPoint.getAmount()).isEqualTo(500L);
    }

    @DisplayName("잔액 충전과 사용이 동시에 들어오면 하나만 수행 되어야 한다.")
    @Test
    void chargeAndUsePointWithOptimisticLock() {
        // given
        Long userId = 1L;
        Point point = Point.create(userId);
        point.charge(1_000L);
        pointRepository.save(point);

        PointCommand.Charge chargeCommand = PointCommand.Charge.of(userId, 500L);
        PointCommand.Use useCommand = PointCommand.Use.of(userId, 300L);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        executeConcurrency(List.of(
            () -> {
                try {
                    pointService.chargePoint(chargeCommand);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                }
            },
            () -> {
                try {
                    pointService.usePoint(useCommand);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                }
            }
        ));

        // then
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);
    }
}