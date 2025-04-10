package kr.hhplus.be.server.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.*;

/*
별도 로직 없으므로 단순 생성만 테스트
 */
class PaymentTest {

    @Test
    @DisplayName("결제 정보는 정상적으로 생성된다")
    void createPayment_success() {
        Payment payment = Payment.of(1L, 1L, BigDecimal.valueOf(10000));

        assertThat(payment.getOrderId()).isEqualTo(1L);
        assertThat(payment.getPaidAmount()).isEqualByComparingTo("10000");
    }
}
