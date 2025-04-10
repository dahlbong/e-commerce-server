package kr.hhplus.be.server.domain.point;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PointHistory {

    private Long id;
    private Long userId;
    private PointHistoryType type; // CHARGE / USE
    private BigDecimal amount;
    private BigDecimal beforeBalance;
    private BigDecimal afterBalance;
    private LocalDateTime createdAt;

    public static PointHistory of(Long id, Long userId, PointHistoryType type,
                                  BigDecimal amount, BigDecimal before, BigDecimal after) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("이력 금액은 0보다 커야 합니다.");
        }
        LocalDateTime now = LocalDateTime.now();
        return new PointHistory(id, userId, type, amount, before, after, now);
    }
}
