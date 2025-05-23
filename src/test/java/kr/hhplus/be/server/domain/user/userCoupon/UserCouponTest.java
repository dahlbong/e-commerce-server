package kr.hhplus.be.server.domain.user.userCoupon;

import kr.hhplus.be.server.domain.user.enums.UserCouponUsedStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserCouponTest {

    @DisplayName("사용할 수 없는 쿠폰인지 확인한다.")
    @Test
    void cannotUse() {
        // given
        UserCoupon userCoupon = UserCoupon.builder()
            .usedStatus(UserCouponUsedStatus.USED)
            .build();

        // when
        boolean isUse = userCoupon.cannotUse();

        // then
        assertThat(isUse).isTrue();
    }
}