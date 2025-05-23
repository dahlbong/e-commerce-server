package kr.hhplus.be.server.domain.point.enums;

import kr.hhplus.be.server.domain.ErrorCode;

public enum PointErrorCode implements ErrorCode {

    INITIAL_BALANCE_NEGATIVE("최초 포인트는 음수일 수 없습니다."),
    CHARGE_AMOUNT_NEGATIVE("충전 금액은 0보다 커야 합니다."),
    USE_AMOUNT_INVALID("사용 금액은 0보다 커야 합니다."),
    INSUFFICIENT_BALANCE("잔액이 부족합니다.");

    private final String message;

    PointErrorCode(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}