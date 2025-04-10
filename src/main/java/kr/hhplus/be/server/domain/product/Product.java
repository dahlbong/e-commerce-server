package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.BusinessException;
import kr.hhplus.be.server.domain.product.enums.ProductErrorCode;
import kr.hhplus.be.server.domain.product.enums.SellingStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Product {

    private final Long id;
    private final String name;
    private final SellingStatus sellingStatus; // SELLING / STOPPED
    private final BigDecimal price;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Product of(Long id, String name, SellingStatus sellingStatus, BigDecimal price) {
        if (name == null || name.isBlank()) {
            throw new BusinessException(ProductErrorCode.NAME_SHOULD_NOT_BE_BLANK);
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ProductErrorCode.PRICE_SHOULD_BE_POSITIVE);
        }
        LocalDateTime now = LocalDateTime.now();
        return new Product(id, name, sellingStatus, price, now, now);
    }

    public boolean isSelling() {
        return this.sellingStatus == SellingStatus.SELLING;
    }
}
