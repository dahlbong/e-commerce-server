package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.supporters.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class PointServiceUnitTest extends MockTestSupport {

    @InjectMocks
    private PointService pointService;

    @Mock
    private PointRepository pointRepository;

    @DisplayName("잔고 충전 시, 충전 금액은 0보다 커야 한다.")
    @Test
    void chargeShouldPositiveAmount() {
        // given
        PointCommand.Charge command = PointCommand.Charge.of(1L, 0L);
        Point point = Point.builder()
            .userId(1L)
            .amount(10_000L)
            .build();

        when(pointRepository.findOptionalByUserId(anyLong()))
            .thenReturn(Optional.of(point));

        // when & then
        assertThatThrownBy(() -> pointService.chargePoint(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("충전 금액은 0보다 커야 합니다.");
    }

    @DisplayName("잔고 충전 시, 최대 금액을 넘을 수 없다.")
    @Test
    void chargeCannotExceedMaxAmount() {
        // given
        PointCommand.Charge command = PointCommand.Charge.of(1L, 1L);
        Point point = Point.builder()
            .userId(1L)
            .amount(10_000_000L)
            .build();

        when(pointRepository.findOptionalByUserId(anyLong()))
            .thenReturn(Optional.of(point));

        // when & then
        assertThatThrownBy(() -> pointService.chargePoint(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("최대 금액을 초과할 수 없습니다.");
    }

    @DisplayName("잔고가 없으면, 잔고를 생성한다.")
    @Test
    void chargePointIfNotExist() {
        // given
        PointCommand.Charge command = PointCommand.Charge.of(1L, 10_000L);
        Point point = Point.builder()
            .userId(1L)
            .amount(10_000L)
            .build();

        when(pointRepository.findOptionalByUserId(anyLong()))
            .thenReturn(Optional.empty());

        when(pointRepository.save(any(Point.class)))
            .thenReturn(point);

        // when
        pointService.chargePoint(command);

        // then
        verify(pointRepository, times(1)).save(any(Point.class));
    }

    @DisplayName("잔고가 있으면, 잔고를 충전한다.")
    @Test
    void chargePointIfExist() {
        // given
        PointCommand.Charge command = mock(PointCommand.Charge.class);
        Point point = mock(Point.class);

        when(pointRepository.findOptionalByUserId(anyLong()))
            .thenReturn(Optional.of(point));

        // when
        pointService.chargePoint(command);

        // then
        verify(point, times(1)).charge(command.getAmount());
        verify(pointRepository, times(0)).save(any(Point.class));
    }

    @DisplayName("잔고가 없으면, 잔고를 차감하지 못한다.")
    @Test
    void usePointIfNotExist() {
        // given
        PointCommand.Use command = PointCommand.Use.of(1L, 10_000L);

        when(pointRepository.findOptionalByUserId(anyLong()))
            .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> pointService.usePoint(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("잔고가 존재하지 않습니다.");
    }

    @DisplayName("사용 금액이 0이면 잔고를 차감하지 못한다.")
    @Test
    void usePointWithZeroAmount() {
        // given
        PointCommand.Use command = PointCommand.Use.of(1L, 0L);
        Point point = Point.builder()
            .userId(1L)
            .amount(10_000L)
            .build();

        when(pointRepository.findOptionalByUserId(anyLong()))
            .thenReturn(Optional.of(point));

        // when & then
        assertThatThrownBy(() -> pointService.usePoint(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("사용 금액은 0보다 커야 합니다.");
    }

    @DisplayName("잔고가 부족하면, 잔고를 차감하지 못한다.")
    @Test
    void usePointWithInsufficientPoint() {
        // given
        PointCommand.Use command = PointCommand.Use.of(1L, 10_001L);
        Point point = Point.builder()
            .userId(1L)
            .amount(10_000L)
            .build();

        when(pointRepository.findOptionalByUserId(anyLong()))
            .thenReturn(Optional.of(point));

        // when & then
        assertThatThrownBy(() -> pointService.usePoint(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("잔액이 부족합니다.");
    }

    @DisplayName("잔고를 차감한다.")
    @Test
    void usePoint() {
        // given
        PointCommand.Use command = PointCommand.Use.of(1L, 10_000L);
        Point point = Point.builder()
            .userId(1L)
            .amount(10_000L)
            .build();

        when(pointRepository.findOptionalByUserId(anyLong()))
            .thenReturn(Optional.of(point));

        // when
        pointService.usePoint(command);

        // then
        assertThat(point.getAmount()).isZero();
    }

    @DisplayName("잔고가 없으면 0을 반환한다.")
    @Test
    void getPointWithNotExist() {
        // given
        when(pointRepository.findOptionalByUserId(anyLong()))
            .thenReturn(Optional.empty());

        // when
        PointInfo.Point pointInfo = pointService.getPoint(1L);

        // then
        assertThat(pointInfo.getAmount()).isZero();
    }

    @DisplayName("잔고를 조회한다.")
    @Test
    void getPoint() {
        // given
        Point point = Point.builder()
            .userId(1L)
            .amount(10_000L)
            .build();

        when(pointRepository.findOptionalByUserId(anyLong()))
            .thenReturn(Optional.of(point));

        // when
        PointInfo.Point pointInfo = pointService.getPoint(1L);

        // then
        assertThat(pointInfo.getAmount()).isEqualTo(10_000L);
    }
}