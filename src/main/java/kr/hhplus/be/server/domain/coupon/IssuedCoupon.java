package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BusinessException;
import kr.hhplus.be.server.domain.coupon.enums.CouponErrorCode;
import kr.hhplus.be.server.domain.coupon.enums.DiscountType;
import kr.hhplus.be.server.domain.coupon.enums.IssuedCouponStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "issued_coupon")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IssuedCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String couponName;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    private BigDecimal discountAmount;

    private LocalDateTime issuedAt;

    private LocalDateTime validStartedAt;

    private LocalDateTime validEndedAt;

    private LocalDateTime usedAt;

    @Enumerated(EnumType.STRING)
    private IssuedCouponStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static IssuedCoupon of(Coupon coupon, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        return new IssuedCoupon(
                userId,
                coupon.getName(),
                coupon.getDiscountType(),
                coupon.getDiscountAmount(),
                now,
                coupon.getValidStartedAt(),
                coupon.getValidEndedAt(),
                null,
                IssuedCouponStatus.UNUSED,
                now,
                now
        );
    }

    private IssuedCoupon(Long userId, String couponName, DiscountType discountType,
                         BigDecimal discountAmount, LocalDateTime issuedAt,
                         LocalDateTime validStartedAt, LocalDateTime validEndedAt,
                         LocalDateTime usedAt, IssuedCouponStatus status,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.couponName = couponName;
        this.discountType = discountType;
        this.discountAmount = discountAmount;
        this.issuedAt = issuedAt;
        this.validStartedAt = validStartedAt;
        this.validEndedAt = validEndedAt;
        this.usedAt = usedAt;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void use() {
        if (status != IssuedCouponStatus.UNUSED) {
            throw new BusinessException(CouponErrorCode.ALREADY_USED);
        }
        this.status = IssuedCouponStatus.USED;
        this.usedAt = LocalDateTime.now();
        this.updatedAt = usedAt;
    }

    public boolean isValidNow(LocalDateTime now) {
        return status == IssuedCouponStatus.UNUSED &&
                !now.isBefore(validStartedAt) &&
                !now.isAfter(validEndedAt);
    }
}
