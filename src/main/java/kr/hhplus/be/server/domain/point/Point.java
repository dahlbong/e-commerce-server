package kr.hhplus.be.server.domain.point;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BusinessException;
import kr.hhplus.be.server.domain.point.enums.PointErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "point")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Point of(Long userId, BigDecimal initialBalance) {
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(PointErrorCode.INITIAL_BALANCE_NEGATIVE);
        }
        Point point = new Point();
        point.userId = userId;
        point.balance = initialBalance;
        point.createdAt = LocalDateTime.now();
        point.updatedAt = point.createdAt;
        return point;
    }


    /**
     * 포인트 충전
     */
    public void charge(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(PointErrorCode.CHARGE_AMOUNT_NEGATIVE);
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