package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.enums.CouponStatus;

import java.util.List;

public interface CouponRepository {
    Coupon findById(Long id);
    void save(Coupon coupon);
    Coupon findByIdWithLock(Long couponId);
    List<Coupon> findByStatus(CouponStatus status);
}
