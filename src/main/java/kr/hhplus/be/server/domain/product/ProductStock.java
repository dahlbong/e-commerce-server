package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.BusinessException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductStock {
    private Long id;
    private Long productId;
    private int quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProductStock of(Long id, Long productId, int quantity) {
        if (quantity < 0) throw new BusinessException(ProductErrorCode.STOCK_SHOULD_NOT_BE_NEGATIVE);
        LocalDateTime now = LocalDateTime.now();
        return new ProductStock(id, productId, quantity, now, now);
    }

    public void decrease(int amount) {
        if (amount <= 0) throw new BusinessException(ProductErrorCode.DECREASE_AMOUNT_SHOULD_BE_POSITIVE);
        if (quantity < amount) throw new BusinessException(ProductErrorCode.OUT_OF_STOCK);
        quantity -= amount;
        updatedAt = LocalDateTime.now();
    }

    public void increase(int amount) {
        quantity += amount;
        updatedAt = LocalDateTime.now();
    }
}
