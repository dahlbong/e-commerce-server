package kr.hhplus.be.server.domain.point;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "point", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id")
})
public class Point {

    private static final long MAX_POINT = 10_000_000L;
    private static final long INIT_POINT = 0L;

    @Id
    @Column(name = "point_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private long amount;

    @Version
    private Long version;

    @Builder
    private Point(Long id, Long userId, long amount) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
    }

    public static Point create(Long userId) {
        return Point.builder()
                .userId(userId)
                .amount(INIT_POINT)
                .build();
    }

    public void charge(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        }

        if (this.amount + amount > MAX_POINT) {
            throw new IllegalArgumentException("최대 금액을 초과할 수 없습니다.");
        }

        this.amount += amount;
    }

    public void use(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("사용 금액은 0보다 커야 합니다.");
        }

        if (this.amount < amount) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }

        this.amount -= amount;
    }
}