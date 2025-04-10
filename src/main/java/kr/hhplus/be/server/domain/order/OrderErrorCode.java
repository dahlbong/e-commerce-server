package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.ErrorCode;

public enum OrderErrorCode implements ErrorCode {
    QUANTITY_SHOULD_BE_POSITIVE("수량은 1 이상이어야 합니다."),
    PRICE_SHOULD_NOT_BE_NEGATIVE("단가는 음수일 수 없습니다.");

    private final String message;

    OrderErrorCode(String message) {
        this.message = message;
    }

    @Override
    public String message() {
        return message;
    }
}
