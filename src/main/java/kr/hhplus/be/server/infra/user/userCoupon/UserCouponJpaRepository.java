package kr.hhplus.be.server.infra.user.userCoupon;

import kr.hhplus.be.server.domain.user.enums.UserCouponUsedStatus;
import kr.hhplus.be.server.domain.user.userCoupon.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCouponJpaRepository extends JpaRepository<UserCoupon, Long> {

    Optional<UserCoupon> findByUserIdAndCouponId(Long userId, Long couponId);

    List<UserCoupon> findByUserIdAndUsedStatusIn(Long userId, List<UserCouponUsedStatus> usedStatuses);

    int countByCouponId(Long couponId);

    List<UserCoupon> findByCouponId(Long couponId);
}