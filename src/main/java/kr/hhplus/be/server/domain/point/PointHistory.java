package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.point.enums.PointHistoryType;
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
        LocalDateTime now = LocalDateTime.now();
        return new PointHistory(id, userId, type, amount, before, after, now);
    }
}
