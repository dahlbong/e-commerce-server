package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.order.enums.PaymentStatus;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String method;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    private final List<Order> orders = new ArrayList<>();

    private Payment(String method, BigDecimal amount) {
        this.method = method;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
        this.paidAt = LocalDateTime.now();
        this.createdAt = paidAt;
        this.updatedAt = paidAt;
    }

    public static Payment of(String method, BigDecimal amount) {
        return new Payment(method, amount);
    }

    public void markAsCompleted() {
        this.status = PaymentStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsFailed() {
        this.status = PaymentStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }
}
