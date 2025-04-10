package kr.hhplus.be.server.domain.coupon;

public interface CouponRepository {
    Coupon findById(Long id);
    void save(Coupon coupon);
}
