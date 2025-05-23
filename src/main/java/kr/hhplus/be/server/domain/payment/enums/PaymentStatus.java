package kr.hhplus.be.server.domain.payment.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {

    READY("결제 준비"),
    WAITING("결제 대기"),
    COMPLETED("결제 완료"),
    FAILED("결제 실패"),
    CANCELLED("결제 취소");

    private final String description;

    private static final List<PaymentStatus> CANNOT_PAYABLE_STATUSES = List.of(COMPLETED, FAILED, CANCELLED);

    public boolean cannotPayable() {
        return CANNOT_PAYABLE_STATUSES.contains(this);
    }

    public static List<PaymentStatus> forCompleted() {
        return List.of(COMPLETED);
    }
}