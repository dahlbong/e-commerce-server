package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.domain.ErrorCode;

public enum UserErrorCode implements ErrorCode {
    BLANK_NAME("사용자 이름은 비어있을 수 없습니다."),
    NOT_FOUND_USER("존재하지 않는 사용자입니다.");

    private final String message;
    UserErrorCode(String message) {
        this.message = message;
    }

    @Override
    public String message() {
        return message;
    }
}