package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.point.enums.PointTransactionType;
import kr.hhplus.be.server.infra.point.PointTransactionJpaRepository;
import kr.hhplus.be.server.supporters.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
class PointServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private PointService pointService;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private PointTransactionJpaRepository pointTransactionJpaRepository;

    @DisplayName("잔고 충전 시, 잔고가 존재하면 충전 금액을 추가한다.")
    @Test
    void chargePointWhenPointExists() {
        // given
        Long userId = 1L;
        Point existingPoint = Point.builder()
            .userId(userId)
            .amount(10_000L)
            .build();
        pointRepository.save(existingPoint);

        PointCommand.Charge command = PointCommand.Charge.of(userId, 5_000L);

        // when
        pointService.chargePoint(command);

        // then
        Point updatedPoint = pointRepository.findOptionalByUserId(userId).orElseThrow();
        assertThat(updatedPoint.getAmount()).isEqualTo(15_000L);
    }

    @DisplayName("잔고 충전 시, 잔고가 존재할 때 충전 금액이 양수이어야 한다.")
    @Test
    void chargePointWhenAmountIsNotPositive() {
        // given
        Long userId = 1L;
        Point existingPoint = Point.builder()
            .userId(userId)
            .amount(10_000L)
            .build();
        pointRepository.save(existingPoint);

        PointCommand.Charge command = PointCommand.Charge.of(userId, -5_000L);

        // when & then
        assertThatThrownBy(() -> pointService.chargePoint(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("충전 금액은 0보다 커야 합니다.");
    }

    @DisplayName("잔고 충전 시, 최대 금액을 초과하면 예외를 발생시킨다.")
    @Test
    void chargePointWhenExceedsMaxAmount() {
        // given
        Long userId = 1L;
        Point existingPoint = Point.builder()
            .userId(userId)
            .amount(10_000_000L)
            .build();
        pointRepository.save(existingPoint);

        PointCommand.Charge command = PointCommand.Charge.of(userId, 1L);

        // when & then
        assertThatThrownBy(() -> pointService.chargePoint(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("최대 금액을 초과할 수 없습니다.");
    }

    @DisplayName("잔고 충전 시, 잔고가 없으면 새 잔고를 생성한다.")
    @Test
    void chargePointWhenPointDoesNotExist() {
        // given
        Long userId = 1L;
        PointCommand.Charge command = PointCommand.Charge.of(userId, 5_000L);

        // when
        pointService.chargePoint(command);

        // then
        Point newPoint = pointRepository.findOptionalByUserId(userId).orElseThrow();
        assertThat(newPoint.getAmount()).isEqualTo(5_000L);
    }

    @DisplayName("잔고 생성 시, 최대 금액을 초과하면 예외를 발생시킨다.")
    @Test
    void createPointWhenExceedsMaxAmount() {
        // given
        Long userId = 1L;
        PointCommand.Charge command = PointCommand.Charge.of(userId, 10_000_001L);

        // when & then
        assertThatThrownBy(() -> pointService.chargePoint(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("최대 금액을 초과할 수 없습니다.");
    }

    @DisplayName("잔고 사용 시, 잔고가 존재하면 사용 금액을 차감한다.")
    @Test
    void usePointWhenPointExists() {
        // given
        Long userId = 1L;
        Point existingPoint = Point.builder()
            .userId(userId)
            .amount(10_000L)
            .build();
        pointRepository.save(existingPoint);

        PointCommand.Use command = PointCommand.Use.of(userId, 5_000L);

        // when
        pointService.usePoint(command);

        // then
        Point updatedPoint = pointRepository.findOptionalByUserId(userId).orElseThrow();
        assertThat(updatedPoint.getAmount()).isEqualTo(5_000L);
    }

    @DisplayName("잔고 사용 시, 잔고가 없으면 예외를 발생시킨다.")
    @Test
    void usePointWhenPointDoseNotExist() {
        // given
        Long userId = 1L;
        PointCommand.Use command = PointCommand.Use.of(userId, 5_000L);

        // when & then
        assertThatThrownBy(() -> pointService.usePoint(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("잔고가 존재하지 않습니다.");
    }

    @DisplayName("잔고 사용 시, 사용 금액은 양수여야 한다.")
    @Test
    void usePointWhenAmountIsNotPositive() {
        // given
        Long userId = 1L;
        Point existingPoint = Point.builder()
            .userId(userId)
            .amount(10_000L)
            .build();
        pointRepository.save(existingPoint);

        PointCommand.Use command = PointCommand.Use.of(userId, -5_000L);

        // when & then
        assertThatThrownBy(() -> pointService.usePoint(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("사용 금액은 0보다 커야 합니다.");
    }

    @DisplayName("잔고 사용 시, 잔고 금액은 충분해야한다.")
    @Test
    void usePointWhenInsufficientPoint() {
        // given
        Long userId = 1L;
        Point existingPoint = Point.builder()
            .userId(userId)
            .amount(5_000L)
            .build();
        pointRepository.save(existingPoint);

        PointCommand.Use command = PointCommand.Use.of(userId, 5_001L);

        // when & then
        assertThatThrownBy(() -> pointService.usePoint(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("잔액이 부족합니다.");
    }

    @DisplayName("잔고 충전 & 사용 시, 트랜잭션 내역을 저장한다.")
    @Test
    void savePointTransactionAfterChargePointAndUsePoint() {
        // given
        Long userId = 1L;
        PointCommand.Charge command = PointCommand.Charge.of(userId, 5_000L);
        PointCommand.Use useCommand = PointCommand.Use.of(userId, 2_000L);

        // when
        pointService.chargePoint(command);
        pointService.usePoint(useCommand);

        // then
        List<PointTransaction> transactions = pointTransactionJpaRepository.findAll();
        assertThat(transactions).hasSize(2)
            .extracting("amount", "transactionType")
            .containsExactly(
                tuple(5_000L, PointTransactionType.CHARGE),
                tuple(-2_000L, PointTransactionType.USE)
            );
    }

    @DisplayName("잔고 조회 시, 잔고가 존재하면 잔고 정보를 반환한다.")
    @Test
    void getPointWhenPointExists() {
        // given
        Long userId = 1L;
        Point existingPoint = Point.builder()
            .userId(userId)
            .amount(10_000L)
            .build();
        pointRepository.save(existingPoint);

        // when
        PointInfo.Point point = pointService.getPoint(userId);

        // then
        assertThat(point.getAmount()).isEqualTo(10_000L);
    }

    @DisplayName("잔고 조회 시, 잔고가 없으면 빈 잔고 정보를 반환한다.")
    @Test
    void getPointWhenPointDoseNotExist() {
        // given
        Long userId = 1L;

        // when
        PointInfo.Point point = pointService.getPoint(userId);

        // then
        assertThat(point.getAmount()).isZero();
    }
}