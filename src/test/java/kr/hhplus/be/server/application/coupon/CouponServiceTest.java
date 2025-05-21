package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.domain.BusinessException;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import kr.hhplus.be.server.domain.coupon.IssuedCouponRepository;
import kr.hhplus.be.server.domain.coupon.enums.CouponErrorCode;
import kr.hhplus.be.server.domain.coupon.enums.DiscountType;
import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock CouponRepository couponRepository;
    @Mock IssuedCouponRepository issuedCouponRepository;
    @Mock UserService userService;

    @InjectMocks
    CouponService couponService;

    private Coupon coupon;
    private User user;

    @BeforeEach
    void setup() {
        coupon = Coupon.of(
                "10% 할인",
                DiscountType.PERCENTAGE,
                BigDecimal.valueOf(10),
                100,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1)
        );
        coupon.assignId(1L); // 테스트용 ID setter

        user = User.of(1L, "사용자");
    }

    @Test
    @DisplayName("쿠폰을 정상적으로 발급받을 수 있다")
    void issueToUser_success() {
        given(userService.getOrCreateById(1L)).willReturn(user);
        given(couponRepository.findById(1L)).willReturn(coupon);
        given(issuedCouponRepository.findByUserId(1L)).willReturn(List.of());

        IssuedCoupon result = couponService.issueToUser(1L, 1L);

        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getCouponName()).isEqualTo("10% 할인");
        assertThat(result.getStatus().name()).isEqualTo("UNUSED");

        verify(issuedCouponRepository).save(any());
    }

    @Test
    @DisplayName("중복 발급 시 ALREADY_ISSUED 예외가 발생한다")
    void issueToUser_duplicate_prevention() {
        IssuedCoupon issued = IssuedCoupon.of(coupon, user.getId());
        given(userService.getOrCreateById(1L)).willReturn(user);
        given(couponRepository.findById(1L)).willReturn(coupon);
        given(issuedCouponRepository.findByUserId(1L)).willReturn(List.of(issued));

        assertThatThrownBy(() -> couponService.issueToUser(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(CouponErrorCode.ALREADY_ISSUED.message());

        verify(issuedCouponRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 쿠폰일 경우 예외가 발생한다")
    void issueToUser_couponNotFound() {
        given(userService.getOrCreateById(999L)).willReturn(User.of(999L, "임시 유저"));
        given(couponRepository.findById(999L))
                .willThrow(new BusinessException(CouponErrorCode.NOT_FOUND_COUPON));

        assertThatThrownBy(() -> couponService.issueToUser(999L, 999L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(CouponErrorCode.NOT_FOUND_COUPON.message());
    }
}
