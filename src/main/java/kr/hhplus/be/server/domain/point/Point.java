package kr.hhplus.be.server.domain.point;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BusinessException;
import kr.hhplus.be.server.domain.point.enums.PointErrorCode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "point")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point {

    private static final long MAX_POINT = 10_000_000L;

    @Id
    @Column(name = "point_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "balance", cascade = CascadeType.ALL)
    private List<PointTransaction> transactions = new ArrayList<>();

    @Builder
    private Point(Long id, Long userId, BigDecimal amount) {
        this.id = id;
        this.userId = userId;
        this.balance = amount;

        addChargeTransaction(amount);
    }

    public static Point of(Long userId, BigDecimal amount) {
        return Point.builder()
                .userId(userId)
                .amount(amount)
                .build();
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
        addChargeTransaction(amount);
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
        addUseTransaction(amount);
    }

    private void addChargeTransaction(BigDecimal amount) {
        PointTransaction transaction = PointTransaction.ofCharge(this, amount);
        this.transactions.add(transaction);
    }

    private void addUseTransaction(BigDecimal amount) {
        PointTransaction transaction = PointTransaction.ofUse(this, amount);
        this.transactions.add(transaction);
    }
}