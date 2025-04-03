package kr.hhplus.be.server.api.controller;

import kr.hhplus.be.server.api.dto.CouponRequest;
import kr.hhplus.be.server.domain.model.Coupon;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    // 더미 쿠폰 저장소 (예시: user1에 대한 데이터)
    private static Map<String, List<Coupon>> couponStore = new HashMap<>();

    static {
        couponStore.put("user1", new ArrayList<>(Arrays.asList(
                new Coupon("CPN001", 10, "2025-12-31", "AVAILABLE"),
                new Coupon("CPN002", 15, "2025-12-31", "USED")
        )));
    }

    // 쿠폰 발급 (중복 발급 방지 로직 포함)
    @PostMapping("/issue")
    public ResponseEntity<Coupon> issueCoupon(@RequestBody CouponRequest request) {
        List<Coupon> userCoupons = couponStore.getOrDefault(request.getUserId(), new ArrayList<>());
        boolean alreadyIssued = userCoupons.stream()
                .anyMatch(c -> c.getId().equals("CPN001") && "AVAILABLE".equals(c.getStatus()));
        if (alreadyIssued) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Coupon newCoupon = new Coupon("CPN001", 10, "2025-12-31", "AVAILABLE");
        userCoupons.add(newCoupon);
        couponStore.put(request.getUserId(), userCoupons);
        return ResponseEntity.ok(newCoupon);
    }

    // 사용자의 쿠폰 목록 조회
    @GetMapping("/{userId}")
    public ResponseEntity<List<Coupon>> getUserCoupons(@PathVariable String userId) {
        List<Coupon> userCoupons = couponStore.get(userId);
        if (userCoupons == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(userCoupons);
    }
}