package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BusinessException;
import kr.hhplus.be.server.domain.product.enums.ProductErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_stock")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductStock {

    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static ProductStock of(Product product, int quantity) {
        if (quantity < 0) {
            throw new BusinessException(ProductErrorCode.STOCK_SHOULD_NOT_BE_NEGATIVE);
        }
        LocalDateTime now = LocalDateTime.now();
        ProductStock stock = new ProductStock();
        stock.product = product;
        stock.quantity = quantity;
        stock.createdAt = now;
        stock.updatedAt = now;

        return stock;
    }

    public void decrease(int amount) {
        if (amount <= 0) {
            throw new BusinessException(ProductErrorCode.DECREASE_AMOUNT_SHOULD_BE_POSITIVE);
        }
        if (quantity < amount) {
            throw new BusinessException(ProductErrorCode.OUT_OF_STOCK);
        }
        this.quantity -= amount;
        this.updatedAt = LocalDateTime.now();
    }

    public void increase(int amount) {
        if (amount <= 0) {
            throw new BusinessException(ProductErrorCode.INCREASE_AMOUNT_SHOULD_BE_POSITIVE);
        }
        this.quantity += amount;
        this.updatedAt = LocalDateTime.now();
    }
}
