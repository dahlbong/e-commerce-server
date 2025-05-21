package kr.hhplus.be.server.domain.point;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.point.enums.PointTransactionType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointTransaction {

    @Id
    @Column(name = "transaction_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long balanceId;

    @Enumerated(EnumType.STRING)
    private PointTransactionType transactionType;

    private long amount;

    @Builder
    private PointTransaction(Long id, Long balanceId, PointTransactionType transactionType, long amount) {
        this.id = id;
        this.balanceId = balanceId;
        this.transactionType = transactionType;
        this.amount = amount;
    }

    public static PointTransaction ofCharge(Point balance, long amount) {
        return PointTransaction.builder()
                .balanceId(balance.getId())
                .transactionType(PointTransactionType.CHARGE)
                .amount(amount)
                .build();
    }

    public static PointTransaction ofUse(Point balance, long amount) {
        return PointTransaction.builder()
                .balanceId(balance.getId())
                .transactionType(PointTransactionType.USE)
                .amount(-amount)
                .build();
    }
}