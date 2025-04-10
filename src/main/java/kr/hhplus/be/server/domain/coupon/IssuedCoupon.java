package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.BusinessException;
import kr.hhplus.be.server.domain.coupon.enums.CouponErrorCode;
import kr.hhplus.be.server.domain.coupon.enums.CouponStatus;
import kr.hhplus.be.server.domain.coupon.enums.IssuedCouponStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class IssuedCoupon {

    private final Long id;
    private final Long couponId;
    private final Long userId;
    private final LocalDateTime issuedAt;
    private LocalDateTime usedAt;
    private IssuedCouponStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static IssuedCoupon of(Coupon coupon, Long userId, Long issuedId) {
        LocalDateTime now = LocalDateTime.now();
        return new IssuedCoupon(
                issuedId, coupon.getId(), userId, now,null, IssuedCouponStatus.UNUSED, now, now
        );
    }

    public void use() {
        if (status == IssuedCouponStatus.USED) {
            throw new BusinessException(CouponErrorCode.ALREADY_USED);
        }
        status = IssuedCouponStatus.USED;
        usedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    public boolean isValidNow(Coupon coupon, LocalDateTime now) {
        return status == IssuedCouponStatus.UNUSED &&
                !now.isBefore(coupon.getValidStartedAt()) &&
                !now.isAfter(coupon.getValidEndedAt()) &&
                coupon.getStatus() == CouponStatus.ACTIVE;
    }
}
