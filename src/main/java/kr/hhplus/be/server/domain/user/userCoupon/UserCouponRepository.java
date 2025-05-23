package kr.hhplus.be.server.domain.user.userCoupon;

import kr.hhplus.be.server.domain.user.enums.UserCouponUsedStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface UserCouponRepository {

    UserCoupon save(UserCoupon userCoupon);

    UserCoupon findByUserIdAndCouponId(Long userId, Long couponId);

    UserCoupon findById(Long userCouponId);

    List<UserCoupon> findByUserIdAndUsableStatusIn(Long userId, List<UserCouponUsedStatus> statuses);

    boolean save(UserCouponCommand.PublishRequest command);

    int countByCouponId(Long couponId);

    List<UserCouponInfo.Candidates> findPublishCandidates(UserCouponCommand.Candidates command);

    void saveAll(List<UserCoupon> userCoupons);

    List<UserCoupon> findCouponId(Long couponId);
}