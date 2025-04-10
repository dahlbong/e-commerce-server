package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BusinessException;
import kr.hhplus.be.server.domain.product.enums.ProductErrorCode;
import kr.hhplus.be.server.domain.product.enums.SellingStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private SellingStatus sellingStatus;

    private BigDecimal price;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "product", fetch = FetchType.LAZY, optional = false)
    private ProductStock stock;

    public static Product of(String name, SellingStatus sellingStatus, BigDecimal price, int initialStockQuantity) {
        if (name == null || name.isBlank()) {
            throw new BusinessException(ProductErrorCode.NAME_SHOULD_NOT_BE_BLANK);
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ProductErrorCode.PRICE_SHOULD_BE_POSITIVE);
        }
        LocalDateTime now = LocalDateTime.now();
        Product product = new Product();
        product.name = name;
        product.sellingStatus = sellingStatus;
        product.price = price;
        product.createdAt = now;
        product.updatedAt = now;

        product.stock = ProductStock.of(product, initialStockQuantity);

        return product;
    }

    public boolean isSelling() {
        return this.sellingStatus == SellingStatus.SELLING;
    }

    public int getRemainQuantity() {
        if (stock == null) return 0;
        return stock.getQuantity();
    }

    public void updatePrice(BigDecimal newPrice) {
        if (newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ProductErrorCode.PRICE_SHOULD_BE_POSITIVE);
        }
        this.price = newPrice;
        this.updatedAt = LocalDateTime.now();
    }

    public void setStockQuantity(int quantity) {
        this.stock.increase(quantity);
    }
}
