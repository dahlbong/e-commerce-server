package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BusinessException;
import kr.hhplus.be.server.domain.order.enums.OrderErrorCode;
import kr.hhplus.be.server.domain.order.enums.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long productId;
    private BigDecimal unitPrice;
    private BigDecimal discountAmount;
    private int quantity;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    private Order(Long userId, Long productId, BigDecimal unitPrice, int quantity) {
        if (quantity <= 0) {
            throw new BusinessException(OrderErrorCode.QUANTITY_SHOULD_BE_POSITIVE);
        }
        this.userId = userId;
        this.productId = productId;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.discountAmount = BigDecimal.ZERO;
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public static Order of(Long userId, Long productId, BigDecimal unitPrice, int quantity) {
        return new Order(userId, productId, unitPrice, quantity);
    }

    public void markAsCompleted() {
        this.status = OrderStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsFailed() {
        this.status = OrderStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal calculateTotalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity)).subtract(discountAmount);
    }

    public void applyDiscount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
        this.updatedAt = LocalDateTime.now();
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
        payment.getOrders().add(this);
    }
}
