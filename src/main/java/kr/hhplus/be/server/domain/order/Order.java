package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BusinessException;
import kr.hhplus.be.server.domain.order.enums.OrderErrorCode;
import kr.hhplus.be.server.domain.order.enums.OrderStatus;
import kr.hhplus.be.server.domain.product.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    private BigDecimal unitPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private BigDecimal discountAmount = BigDecimal.ZERO;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    public static Order of(Long userId, Product product, int quantity) {
        if (quantity <= 0) {
            throw new BusinessException(OrderErrorCode.QUANTITY_SHOULD_BE_POSITIVE);
        }

        Order order = new Order();
        order.userId = userId;
        order.product = product;
        order.quantity = quantity;
        order.unitPrice = product.getPrice();
        order.status = OrderStatus.PENDING;
        order.createdAt = LocalDateTime.now();
        order.updatedAt = LocalDateTime.now();
        return order;
    }

    public void applyDiscount(BigDecimal discount) {
        this.discountAmount = discount;
        this.updatedAt = LocalDateTime.now();
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsCompleted() {
        this.status = OrderStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal calculateTotalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity)).subtract(discountAmount);
    }

    public Long getProductId() {
        return product.getId();
    }
}
