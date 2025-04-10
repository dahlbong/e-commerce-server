package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.application.user.UserService;
import kr.hhplus.be.server.domain.BusinessException;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import kr.hhplus.be.server.domain.coupon.IssuedCouponRepository;
import kr.hhplus.be.server.domain.coupon.enums.CouponErrorCode;
import kr.hhplus.be.server.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final IssuedCouponRepository issuedCouponRepository;
    private final UserService userService;

    public IssuedCoupon issueToUser(Long couponId, Long userId) {
        User user = userService.getOrCreateById(userId);
        Coupon coupon = couponRepository.findById(couponId);

        List<IssuedCoupon> issuedCoupons = issuedCouponRepository.findByUserId(userId);
        boolean alreadyIssued = issuedCoupons.stream()
                .anyMatch(c -> c.getCouponName().equals(coupon.getName()) &&
                        c.getUserId().equals(user.getId()));

        if (alreadyIssued) {
            throw new BusinessException(CouponErrorCode.ALREADY_ISSUED);
        }

        coupon.issue();

        IssuedCoupon issued = IssuedCoupon.of(coupon, user.getId());
        issuedCouponRepository.save(issued);
        return issued;
    }

    public List<IssuedCoupon> getAvailableCoupons(Long userId) {
        return issuedCouponRepository.findByUserId(userId);
    }
}
