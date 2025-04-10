package kr.hhplus.be.server.api.coupon;

import kr.hhplus.be.server.application.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    /**
     * 선착순 쿠폰 발급
     */
    @PostMapping("/{couponId}/issue")
    public IssuedCouponResponse issue(
            @PathVariable Long couponId,
            @RequestParam Long userId
    ) {
        IssuedCoupon issued = couponService.issueToUser(couponId, userId);
        return IssuedCouponResponse.from(issued);
    }

    /**
     * 사용자 보유 쿠폰 목록
     */
    @GetMapping("/issued")
    public List<IssuedCouponResponse> getIssuedCoupons(
            @RequestParam Long userId
    ) {
        return couponService.getAvailableCoupons(userId).stream()
                .map(IssuedCouponResponse::from)
                .toList();
    }
}
