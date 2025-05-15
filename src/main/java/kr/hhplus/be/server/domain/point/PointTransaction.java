package kr.hhplus.be.server.domain.point;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.point.enums.PointTransactionType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "point_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private PointTransactionType type; // CHARGE / USE

    private BigDecimal amount;
    private BigDecimal beforeBalance;
    private BigDecimal afterBalance;
    private LocalDateTime createdAt;

    public static PointHistory of(Long userId, PointTransactionType type, BigDecimal amount, BigDecimal before, BigDecimal after) {
        PointHistory history = new PointHistory();
        history.userId = userId;
        history.type = type;
        history.amount = amount;
        history.beforeBalance = before;
        history.afterBalance = after;
        history.createdAt = LocalDateTime.now();
        return history;
    }
}
