package kr.hhplus.be.server.infra.coupon;

import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssuedCouponJpaRepository extends JpaRepository<IssuedCoupon, Long> {
    List<IssuedCoupon> findAllByUserId(Long userId);
}
