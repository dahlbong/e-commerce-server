package kr.hhplus.be.server.domain.order;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Payment {

    private final Long id;
    private final Long orderId;
    private final String paymentMethod; // 현재는 POINT로 고정
    private final BigDecimal paidAmount;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Payment of(Long id, Long orderId, BigDecimal paidAmount) {
        LocalDateTime now = LocalDateTime.now();
        return new Payment(id, orderId, "POINT", paidAmount, now, now);
    }
}
