package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.PointHistoryRepository;
import kr.hhplus.be.server.domain.point.enums.PointHistoryType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PointHistoryServiceTest {

    @Mock
    PointHistoryRepository pointHistoryRepository;

    @InjectMocks
    PointHistoryService pointHistoryService;

    @Test
    @DisplayName("유저 ID로 포인트 이력 목록을 조회할 수 있다")
    void getHistoriesByUserId_returns_list() {
        // given
        Long userId = 1L;
        List<PointHistory> mockHistories = List.of(
                PointHistory.of(userId, PointHistoryType.CHARGE, BigDecimal.valueOf(1000), BigDecimal.ZERO, BigDecimal.valueOf(1000)),
                PointHistory.of(userId, PointHistoryType.USE, BigDecimal.valueOf(500), BigDecimal.valueOf(1000), BigDecimal.valueOf(500))
        );
        given(pointHistoryRepository.findAllByUserId(userId)).willReturn(mockHistories);

        // when
        List<PointHistory> result = pointHistoryService.getHistories(userId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getType()).isEqualTo(PointHistoryType.CHARGE);
        assertThat(result.get(1).getType()).isEqualTo(PointHistoryType.USE);
    }
}
