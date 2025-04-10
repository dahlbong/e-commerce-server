package kr.hhplus.be.server.api.coupon;

import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import kr.hhplus.be.server.domain.coupon.enums.DiscountType;
import kr.hhplus.be.server.domain.coupon.enums.IssuedCouponStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record IssuedCouponResponse(
        Long id,
        String couponName,
        DiscountType discountType,
        BigDecimal discountAmount,
        IssuedCouponStatus status,
        LocalDateTime validStartedAt,
        LocalDateTime validEndedAt,
        LocalDateTime usedAt
) {
    public static IssuedCouponResponse from(IssuedCoupon coupon) {
        return new IssuedCouponResponse(
                coupon.getId(),
                coupon.getCouponName(),
                coupon.getDiscountType(),
                coupon.getDiscountAmount(),
                coupon.getStatus(),
                coupon.getValidStartedAt(),
                coupon.getValidEndedAt(),
                coupon.getUsedAt()
        );
    }
}
