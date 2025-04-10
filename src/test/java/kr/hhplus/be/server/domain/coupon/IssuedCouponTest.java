package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.BusinessException;
import kr.hhplus.be.server.domain.coupon.enums.CouponErrorCode;
import kr.hhplus.be.server.domain.coupon.enums.DiscountType;
import kr.hhplus.be.server.domain.coupon.enums.IssuedCouponStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class IssuedCouponTest {

    @Test
    @DisplayName("IssuedCoupon이 정상적으로 생성된다")
    void createIssuedCoupon_success() {
        Coupon coupon = Coupon.of(
                1L, "10% 할인", DiscountType.PERCENTAGE,
                BigDecimal.valueOf(10), 100,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(3)
        );

        IssuedCoupon issued = IssuedCoupon.of(coupon, 999L, 10L);

        assertThat(issued.getCouponId()).isEqualTo(coupon.getId());
        assertThat(issued.getUserId()).isEqualTo(999L);
        assertThat(issued.getStatus()).isEqualTo(IssuedCouponStatus.UNUSED);
    }

    @Test
    @DisplayName("UNUSED 상태의 쿠폰을 사용 처리하면 USED로 상태가 변경된다")
    void useCoupon_success() {
        Coupon coupon = Coupon.of(
                1L, "5,000원 할인", DiscountType.FIXED_AMOUNT,
                BigDecimal.valueOf(5000), 100,
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1)
        );
        IssuedCoupon issued = IssuedCoupon.of(coupon, 999L, 10L);

        issued.use();

        assertThat(issued.getStatus()).isEqualTo(IssuedCouponStatus.USED);
        assertThat(issued.getUsedAt()).isNotNull();
    }

    @Test
    @DisplayName("이미 사용된 쿠폰을 다시 사용하려고 하면 ALREADY_USED 예외메시지를 던진다")
    void useCoupon_fail_alreadyUsed() {
        Coupon coupon = Coupon.of(
                1L, "한번만 쿠폰", DiscountType.FIXED_AMOUNT,
                BigDecimal.valueOf(1000), 100,
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1)
        );
        IssuedCoupon issued = IssuedCoupon.of(coupon, 123L, 10L);
        issued.use();

        assertThatThrownBy(issued::use)
                .isInstanceOf(BusinessException.class)
                .hasMessage(CouponErrorCode.ALREADY_USED.message());
    }

    @Test
    @DisplayName("UNUSED 상태이면서 유효기간 내이고 쿠폰이 ACTIVE면 isValidNow는 true")
    void isValidNow_success() {
        Coupon coupon = Coupon.of(
                1L, "기간 내 쿠폰", DiscountType.PERCENTAGE,
                BigDecimal.valueOf(5), 10,
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1)
        );
        IssuedCoupon issued = IssuedCoupon.of(coupon, 1L, 1L);

        assertThat(issued.isValidNow(coupon, LocalDateTime.now())).isTrue();
    }

    @Test
    @DisplayName("기간이 지나거나 쿠폰이 ACTIVE가 아니면 isValidNow는 false")
    void isValidNow_fail_by_coupon() {
        Coupon coupon = Coupon.of(
                1L, "기간 지난 쿠폰", DiscountType.FIXED_AMOUNT,
                BigDecimal.valueOf(5000), 10,
                LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(5)
        );
        IssuedCoupon issued = IssuedCoupon.of(coupon, 1L, 1L);

        assertThat(issued.isValidNow(coupon, LocalDateTime.now())).isFalse();
    }
}
