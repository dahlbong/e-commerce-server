package kr.hhplus.be.server.domain.point;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.point.enums.PointTransactionType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointTransaction {

    @Id
    @Column(name = "transaction_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "balance_id")
    private Point balance;

    @Enumerated(EnumType.STRING)
    private PointTransactionType type;

    private BigDecimal amount;

    @Builder
    private PointTransaction(Long id, Point balance, PointTransactionType type, BigDecimal amount) {
        this.id = id;
        this.balance = balance;
        this.type = type;
        this.amount = amount;
    }

    public static PointTransaction ofCharge(Point balance, BigDecimal amount) {
        return PointTransaction.builder()
                .balance(balance)
                .type(PointTransactionType.CHARGE)
                .amount(amount)
                .build();
    }

    public static PointTransaction ofUse(Point balance, BigDecimal amount) {
        return PointTransaction.builder()
                .balance(balance)
                .type(PointTransactionType.USE)
                .amount(amount.negate())
                .build();
    }
}