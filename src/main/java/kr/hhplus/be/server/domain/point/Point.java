package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.BusinessException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Point {
    private Long id;
    private Long userId;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Point of(Long id, Long userId, BigDecimal initialBalance) {
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(PointErrorCode.INITIAL_BALANCE_NEGATIVE);
        }
        LocalDateTime now = LocalDateTime.now();
        return new Point(id, userId, initialBalance, now, now);
    }

    /**
     * 포인트 충전
     */
    public void charge(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(PointErrorCode.CHARGE_AMOUNT_INVALID);
        }
        this.balance = this.balance.add(amount);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 포인트 사용
     */
    public void use(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(PointErrorCode.USE_AMOUNT_INVALID);
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new BusinessException(PointErrorCode.INSUFFICIENT_BALANCE);
        }
        this.balance = this.balance.subtract(amount);
        this.updatedAt = LocalDateTime.now();
    }
}