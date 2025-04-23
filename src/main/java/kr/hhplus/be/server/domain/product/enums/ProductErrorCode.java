package kr.hhplus.be.server.domain.product.enums;

import kr.hhplus.be.server.domain.ErrorCode;

public enum ProductErrorCode implements ErrorCode {
    PRICE_SHOULD_BE_POSITIVE("상품 가격은 0보다 커야 합니다."),
    NAME_SHOULD_NOT_BE_BLANK("상품 이름은 비어 있을 수 없습니다."),

    STOCK_SHOULD_NOT_BE_NEGATIVE("초기 재고는 0 이상이어야 합니다."),
    DECREASE_AMOUNT_SHOULD_BE_POSITIVE("재고 감소 수량은 0보다 커야합니다."),
    OUT_OF_STOCK("재고가 부족합니다."),
    INCREASE_AMOUNT_SHOULD_BE_POSITIVE("재고 추가 수량은 0보다 커야합니다."),
    PRODUCT_NOT_FOUND("상품을 찾을 수 없습니다."),

    POPULAR_PRODUCT_UPDATE_FAILED("인기 상품 업데이트에 실패했습니다.");

    private final String message;
    ProductErrorCode(String message) {
        this.message = message;
    }

    @Override
    public String message() {
        return message;
    }
}
