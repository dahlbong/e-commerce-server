package kr.hhplus.be.server.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.api.dto.CouponRequest;
import kr.hhplus.be.server.domain.model.Coupon;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/coupons")
@Tag(name = "Coupon API", description = "쿠폰 발급 및 조회 API")
public class CouponController {

    private static Map<String, List<Coupon>> couponStore = new HashMap<>();

    static {
        couponStore.put("user1", new ArrayList<>(Arrays.asList(
                new Coupon("CPN001", 10, "2025-12-31", "AVAILABLE"),
                new Coupon("CPN002", 15, "2025-12-31", "USED")
        )));
    }

    @Operation(summary = "쿠폰 발급", description = "사용자에게 선착순으로 쿠폰을 발급합니다. 중복 발급은 방지됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "쿠폰 발급 성공"),
            @ApiResponse(responseCode = "400", description = "이미 발급된 사용자",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"status\":400,\"message\":\"Coupon already issued\"}")))
    })
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

    @Operation(summary = "사용자 보유 쿠폰 목록 조회", description = "사용자가 보유한 쿠폰 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "쿠폰 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 사용자의 쿠폰이 존재하지 않음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"status\":404,\"message\":\"No coupons found\"}")))
    })
    @GetMapping("/{userId}")
    public ResponseEntity<List<Coupon>> getUserCoupons(@PathVariable String userId) {
        List<Coupon> userCoupons = couponStore.get(userId);
        if (userCoupons == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(userCoupons);
    }
}