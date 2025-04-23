package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BusinessException;
import kr.hhplus.be.server.domain.coupon.enums.CouponErrorCode;
import kr.hhplus.be.server.domain.coupon.enums.CouponStatus;
import kr.hhplus.be.server.domain.coupon.enums.DiscountType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupon")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    private BigDecimal discountAmount;

    private int maxCapacity;

    private int remainCapacity;

    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    private LocalDateTime validStartedAt;

    private LocalDateTime validEndedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static Coupon of(String name, DiscountType discountType, BigDecimal discountAmount,
                            int maxCapacity, LocalDateTime validStartedAt, LocalDateTime validEndedAt) {
        if (discountAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(CouponErrorCode.DISCOUNT_AMOUNT_SHOULD_BE_POSITIVE);
        }
        if (validStartedAt.isAfter(validEndedAt)) {
            throw new BusinessException(CouponErrorCode.INVALID_VALID_PERIOD);
        }
        if (maxCapacity <= 0) {
            throw new BusinessException(CouponErrorCode.MAX_CAPACITY_SHOULD_BE_POSITIVE);
        }

        LocalDateTime now = LocalDateTime.now();

        Coupon coupon = new Coupon();
        coupon.name = name;
        coupon.discountType = discountType;
        coupon.discountAmount = discountAmount;
        coupon.maxCapacity = maxCapacity;
        coupon.remainCapacity = maxCapacity;
        coupon.status = CouponStatus.ACTIVE;
        coupon.validStartedAt = validStartedAt;
        coupon.validEndedAt = validEndedAt;
        coupon.createdAt = now;
        coupon.updatedAt = now;
        return coupon;
    }

    public void issue() {
        if (remainCapacity <= 0 || status != CouponStatus.ACTIVE) {
            throw new BusinessException(CouponErrorCode.OUT_OF_CAPACITY);
        }
        this.remainCapacity--;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isValidNow(LocalDateTime now) {
        return status == CouponStatus.ACTIVE
                && !now.isBefore(validStartedAt)
                && !now.isAfter(validEndedAt);
    }

    public Coupon assignId(Long id) {
        this.id = id;
        return this;
    }
}
