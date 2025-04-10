package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.BusinessException;
import kr.hhplus.be.server.domain.coupon.enums.CouponErrorCode;
import kr.hhplus.be.server.domain.coupon.enums.CouponStatus;
import kr.hhplus.be.server.domain.coupon.enums.DiscountType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Coupon {

    private final Long id;
    private final String name;
    private final DiscountType discountType;
    private final BigDecimal discountAmount;
    private final int maxCapacity;
    private int remainCapacity;
    private final LocalDateTime validStartedAt;
    private final LocalDateTime validEndedAt;
    private CouponStatus status;
    private final int version;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Coupon of(Long id, String name, DiscountType discountType, BigDecimal discountAmount,
                            int maxCapacity, LocalDateTime start, LocalDateTime end) {
        if (discountAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(CouponErrorCode.DISCOUNT_AMOUNT_SHOULD_BE_POSITIVE);
        }
        if (maxCapacity <= 0) {
            throw new BusinessException(CouponErrorCode.MAX_CAPACITY_SHOULD_BE_POSITIVE);
        }
        LocalDateTime now = LocalDateTime.now();
        return new Coupon(id, name, discountType, discountAmount, maxCapacity, maxCapacity, start, end,
                CouponStatus.ACTIVE, 0, now, now);
    }

    public void issue() {
        if (status != CouponStatus.ACTIVE) {
            throw new BusinessException(CouponErrorCode.COUPON_INACTIVATED);
        }
        if (remainCapacity <= 0) {
            throw new BusinessException(CouponErrorCode.OUT_OF_CAPACITY);
        }
        remainCapacity--;
        updatedAt = LocalDateTime.now();
    }

    public boolean isValidNow(LocalDateTime now) {
        return status == CouponStatus.ACTIVE &&
                !now.isBefore(validStartedAt) &&
                !now.isAfter(validEndedAt);
    }
}
