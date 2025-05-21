package kr.hhplus.be.server.domain.payment.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentMethod {

    CARD("카드"),
    CASH("현금"),
    KAKAO_PAY("카카오페이"),
    UNKNOWN("알 수 없음");

    private final String description;
}