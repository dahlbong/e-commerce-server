package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.point.enums.PointErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class PointTest {

    @Nested
    class charge {
        @Test
        @DisplayName("포인트 생성 시 초기 잔액이 올바르게 설정된다")
        void createPoint_success() {
            Point point = Point.of(1L, 1001L, BigDecimal.valueOf(1000));

            assertThat(point.getUserId()).isEqualTo(1001L);
            assertThat(point.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(1000));
        }

        @Test
        @DisplayName("포인트 생성 시 초기 잔액이 음수이면 INITIAL_BALANCE_NEGATIVE 에러 메시지를 던진다")
        void createPoint_fail_negativeBalance() {
            assertThatThrownBy(() -> Point.of(1L, 1001L, BigDecimal.valueOf(-100)))
                    .hasMessage(PointErrorCode.INITIAL_BALANCE_NEGATIVE.message());
        }

        @Test
        @DisplayName("포인트 충전 시 잔액이 정상적으로 증가한다")
        void chargePoint_success() {
            Point point = Point.of(1L, 1001L, BigDecimal.valueOf(1000));
            point.charge(BigDecimal.valueOf(500));

            assertThat(point.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(1500));
        }

        @Test
        @DisplayName("포인트 충전 시 0 이하의 금액이면 CHARGE_AMOUNT_INVALID 에러 메시지를 던진다")
        void chargePoint_fail_zeroOrNegative() {
            Point point = Point.of(1L, 1001L, BigDecimal.valueOf(1000));

            assertThatThrownBy(() -> point.charge(BigDecimal.ZERO))
                    .hasMessage(PointErrorCode.CHARGE_AMOUNT_INVALID.message());

            assertThatThrownBy(() -> point.charge(BigDecimal.valueOf(-100)))
                    .hasMessage(PointErrorCode.CHARGE_AMOUNT_INVALID.message());;
        }
    }

    @Nested
    class use {
        @Test
        @DisplayName("포인트 사용 시 잔액이 정상적으로 차감된다")
        void usePoint_success() {
            Point point = Point.of(1L, 1001L, BigDecimal.valueOf(1000));
            point.use(BigDecimal.valueOf(300));

            assertThat(point.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(700));
        }

        @Test
        @DisplayName("포인트 사용 시 잔액보다 큰 금액을 사용하면 INSUFFICIENT_BALANCE 에러 메시지를 던진다")
        void usePoint_fail_insufficientBalance() {
            Point point = Point.of(1L, 1001L, BigDecimal.valueOf(300));

            assertThatThrownBy(() -> point.use(BigDecimal.valueOf(500)))
                    .hasMessage(PointErrorCode.INSUFFICIENT_BALANCE.message());
        }

        @Test
        @DisplayName("포인트 사용 시 0 이하의 금액을 사용하면 USE_AMOUNT_INVALID 에러 메시지를 던진다")
        void usePoint_fail_zeroOrNegativeAmount() {
            Point point = Point.of(1L, 1001L, BigDecimal.valueOf(1000));

            assertThatThrownBy(() -> point.use(BigDecimal.valueOf(-50)))
                    .hasMessage(PointErrorCode.USE_AMOUNT_INVALID.message());
        }
    }

}
