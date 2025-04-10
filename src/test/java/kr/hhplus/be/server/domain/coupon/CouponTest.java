package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.BusinessException;
import kr.hhplus.be.server.domain.coupon.enums.CouponErrorCode;
import kr.hhplus.be.server.domain.coupon.enums.CouponStatus;
import kr.hhplus.be.server.domain.coupon.enums.DiscountType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class CouponTest {

    @Test
    @DisplayName("쿠폰이 정상적으로 생성된다")
    void createCoupon_success() {
        Coupon coupon = Coupon.of(
                1L, "10% 할인쿠폰", DiscountType.PERCENTAGE,
                BigDecimal.valueOf(10),
                100,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(5)
        );

        assertThat(coupon.getName()).isEqualTo("10% 할인쿠폰");
        assertThat(coupon.getRemainCapacity()).isEqualTo(100);
        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.ACTIVE);
    }

    @Test
    @DisplayName("할인 금액이 0 이하일 경우 DISCOUNT_AMOUNT_SHOULD_BE_POSITIVE 예외메시지를 던진다")
    void createCoupon_fail_invalidDiscountAmount() {
        assertThatThrownBy(() -> Coupon.of(
                1L, "잘못된 쿠폰", DiscountType.PERCENTAGE,
                BigDecimal.ZERO, 100,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1)
        )).isInstanceOf(BusinessException.class)
                .hasMessage(CouponErrorCode.DISCOUNT_AMOUNT_SHOULD_BE_POSITIVE.message());
    }

    @Test
    @DisplayName("최대 발급 수량이 0 이하일 경우 MAX_CAPACITY_SHOULD_BE_POSITIVE 예외메시지를 던진다")
    void createCoupon_fail_invalidMaxCapacity() {
        assertThatThrownBy(() -> Coupon.of(
                1L, "용량 부족 쿠폰", DiscountType.FIXED_AMOUNT,
                BigDecimal.valueOf(1000), 0,
                LocalDateTime.now(), LocalDateTime.now().plusDays(5)
        )).isInstanceOf(BusinessException.class)
                .hasMessage(CouponErrorCode.MAX_CAPACITY_SHOULD_BE_POSITIVE.message());
    }

    @Test
    @DisplayName("정상 쿠폰 발급 시 잔여 수량이 감소한다")
    void issueCoupon_success() {
        Coupon coupon = Coupon.of(
                1L, "발급용 쿠폰", DiscountType.FIXED_AMOUNT,
                BigDecimal.valueOf(1000), 10,
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1)
        );

        coupon.issue();

        assertThat(coupon.getRemainCapacity()).isEqualTo(9);
    }

    @Test
    @DisplayName("쿠폰이 비활성화 상태일 경우 발급 시 OUT_OF_CAPACITY 예외메시지를 던진다")
    void issueCoupon_fail_inactive() {
        Coupon coupon = Coupon.of(
                1L, "비활성 쿠폰", DiscountType.FIXED_AMOUNT,
                BigDecimal.valueOf(1000), 5,
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1)
        );
        coupon.issue(); // 1번 발급은 성공
        coupon.issue(); // 2번
        coupon.issue(); // 3번
        coupon.issue(); // 4번
        coupon.issue(); // 5번 - 이제 0장 남음

        assertThatThrownBy(coupon::issue)
                .isInstanceOf(BusinessException.class)
                .hasMessage(CouponErrorCode.OUT_OF_CAPACITY.message());
    }

    @Test
    @DisplayName("유효한 시간 내 ACTIVE 상태일 경우 isValidNow는 true를 반환한다")
    void isValidNow_success() {
        Coupon coupon = Coupon.of(
                1L, "기간 내 쿠폰", DiscountType.PERCENTAGE,
                BigDecimal.valueOf(5), 10,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1)
        );

        assertThat(coupon.isValidNow(LocalDateTime.now())).isTrue();
    }

    @Test
    @DisplayName("유효기간이 지난 쿠폰은 isValidNow가 false를 반환한다")
    void isValidNow_fail_expired() {
        Coupon coupon = Coupon.of(
                1L, "기간 지난 쿠폰", DiscountType.PERCENTAGE,
                BigDecimal.valueOf(5), 10,
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now().minusDays(1)
        );

        assertThat(coupon.isValidNow(LocalDateTime.now())).isFalse();
    }
}
