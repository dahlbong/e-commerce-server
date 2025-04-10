package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.enums.PaymentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class PaymentTest {

    @Test
    @DisplayName("결제 정보는 정상적으로 생성된다")
    void createPayment_success() {
        Payment payment = Payment.of("POINT", BigDecimal.valueOf(10000));

        assertThat(payment.getAmount()).isEqualByComparingTo("10000");
        assertThat(payment.getMethod()).isEqualTo("POINT");
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
    }

    @Test
    @DisplayName("결제를 성공 상태로 전이시킬 수 있다")
    void mark_completed() {
        Payment payment = Payment.of("CARD", BigDecimal.valueOf(20000));
        payment.markAsCompleted();

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    @Test
    @DisplayName("결제를 실패 상태로 전이시킬 수 있다")
    void mark_failed() {
        Payment payment = Payment.of("CARD", BigDecimal.valueOf(20000));
        payment.markAsFailed();

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }
}
