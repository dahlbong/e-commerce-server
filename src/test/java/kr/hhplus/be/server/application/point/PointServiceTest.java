package kr.hhplus.be.server.application.point;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.point.PointTransaction;
import kr.hhplus.be.server.domain.point.PointHistoryRepository;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.point.enums.PointTransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    PointRepository pointRepository;

    @Mock
    PointHistoryRepository pointHistoryRepository;

    @InjectMocks
    PointService pointService;

    Point userPoint;

    @BeforeEach
    void setup() {
        userPoint = Point.of(1L, BigDecimal.valueOf(1000));
    }

    @Test
    @DisplayName("포인트를 정상적으로 조회할 수 있다")
    void get_returns_point() {
        // given
        given(pointRepository.findByUserId(1L)).willReturn(userPoint);

        // when
        Point result = pointService.get(1L);

        // then
        assertThat(result).isEqualTo(userPoint);
    }


    @Test
    @DisplayName("정상적으로 포인트를 충전할 수 있다")
    void charge_successfully() {
        // given
        given(pointRepository.findByUserId(1L)).willReturn(userPoint);

        // when
        Point result = pointService.charge(1L, BigDecimal.valueOf(500));

        // then
        assertThat(result.getBalance()).isEqualByComparingTo("1500");
        verify(pointRepository).save(userPoint);

        ArgumentCaptor<PointTransaction> captor = ArgumentCaptor.forClass(PointTransaction.class);
        verify(pointHistoryRepository).save(captor.capture());
        PointTransaction history = captor.getValue();

        assertThat(history.getUserId()).isEqualTo(1L);
        assertThat(history.getAmount()).isEqualByComparingTo("500");
        assertThat(history.getType()).isEqualTo(PointTransactionType.CHARGE);
        assertThat(history.getBeforeBalance()).isEqualByComparingTo("1000");
        assertThat(history.getAfterBalance()).isEqualByComparingTo("1500");
    }

    @Test
    @DisplayName("정상적으로 포인트를 사용할 수 있다")
    void use_successfully() {
        given(pointRepository.findByUserId(1L)).willReturn(userPoint);

        Point result = pointService.use(1L, BigDecimal.valueOf(400));

        assertThat(result.getBalance()).isEqualByComparingTo("600");
        verify(pointRepository).save(userPoint);

        ArgumentCaptor<PointTransaction> captor = ArgumentCaptor.forClass(PointTransaction.class);
        verify(pointHistoryRepository).save(captor.capture());
        PointTransaction history = captor.getValue();

        assertThat(history.getUserId()).isEqualTo(1L);
        assertThat(history.getAmount()).isEqualByComparingTo("400");
        assertThat(history.getType()).isEqualTo(PointTransactionType.USE);
        assertThat(history.getBeforeBalance()).isEqualByComparingTo("1000");
        assertThat(history.getAfterBalance()).isEqualByComparingTo("600");
    }

}