package kr.hhplus.be.server.domain.coupon.enums;

import kr.hhplus.be.server.domain.ErrorCode;

public enum CouponErrorCode implements ErrorCode {
    DISCOUNT_AMOUNT_SHOULD_BE_POSITIVE("할인 금액은 0보다 커야합니다."),
    MAX_CAPACITY_SHOULD_BE_POSITIVE("최대 발급 수량은 0보다 커야합니다."),
    OUT_OF_CAPACITY("발급 가능한 쿠폰 수량이 초과되었습니다."),
    COUPON_INACTIVATED("비활성화된 쿠폰은 발급할 수 없습니다."),

    ALREADY_USED("이미 사용한 쿠폰입니다.");

    private final String message;

    CouponErrorCode(String message) {
        this.message = message;
    }

    @Override
    public String message() {
        return message;
    }
}
