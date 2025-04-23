package kr.hhplus.be.server.api.coupon;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.application.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "쿠폰", description = "쿠폰 발급 및 보유 쿠폰 조회 API")
@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;


    @Operation(summary = "쿠폰 발급", description = "사용자에게 선착순으로 쿠폰을 발급합니다.")
    @PostMapping("/{couponId}/issue")
    public IssuedCouponResponse issue(
            @PathVariable Long couponId,
            @RequestParam Long userId
    ) {
        IssuedCoupon issued = couponService.issueToUser(couponId, userId);
        return IssuedCouponResponse.from(issued);
    }

    @Operation(summary = "보유 쿠폰 조회", description = "사용자가 보유한 모든 쿠폰 목록을 조회합니다.")
    @GetMapping("/issued")
    public List<IssuedCouponResponse> getIssuedCoupons(
            @RequestParam Long userId
    ) {
        return couponService.getAvailableCoupons(userId).stream()
                .map(IssuedCouponResponse::from)
                .toList();
    }
}
