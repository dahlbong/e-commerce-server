package kr.hhplus.be.server.domain.order;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Order {
    private Long id;
    private Long orderId;
    private Long productId;
    private int quantity;
    private BigDecimal price;
    private BigDecimal discountAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Order of(Long id, Long orderId, Long productId, int quantity, BigDecimal price, BigDecimal discountAmt) {
        if (quantity <= 0) throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        if (price.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("가격은 음수일 수 없습니다.");
        LocalDateTime now = LocalDateTime.now();
        return new Order(id, orderId, productId, quantity, price, discountAmt, now, now);
    }

    public BigDecimal calculateTotalPrice() {
        return price.multiply(BigDecimal.valueOf(quantity)).subtract(discountAmount);
    }
}