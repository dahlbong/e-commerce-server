package kr.hhplus.be.server.domain.coupon;

import java.util.List;

public interface IssuedCouponRepository {
    void save(IssuedCoupon issuedCoupon);
    IssuedCoupon findById(Long id);
    List<IssuedCoupon> findByUserId(Long userId);
}
