package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.point.enums.PointHistoryType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class PointHistoryTest {

    @Test
    @DisplayName("포인트 이력이 정상적으로 생성된다")
    void createPointHistory_success() {
        PointHistory history = PointHistory.of(
                1L,
                1001L,
                PointHistoryType.CHARGE,
                BigDecimal.valueOf(500),
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(1500)
        );

        assertThat(history.getUserId()).isEqualTo(1001L);
        assertThat(history.getType()).isEqualTo(PointHistoryType.CHARGE);
        assertThat(history.getAmount()).isEqualByComparingTo("500");
        assertThat(history.getBeforeBalance()).isEqualByComparingTo("1000");
        assertThat(history.getAfterBalance()).isEqualByComparingTo("1500");
    }
}
