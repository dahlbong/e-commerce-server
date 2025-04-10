package kr.hhplus.be.server.infra.coupon;

import kr.hhplus.be.server.domain.BusinessException;
import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import kr.hhplus.be.server.domain.coupon.IssuedCouponRepository;
import kr.hhplus.be.server.domain.coupon.enums.CouponErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class IssuedCouponRepositoryImpl implements IssuedCouponRepository {

    private final IssuedCouponJpaRepository jpa;

    @Override
    public void save(IssuedCoupon issuedCoupon) {
        jpa.save(issuedCoupon);
    }

    @Override
    public IssuedCoupon findById(Long id) {
        return jpa.findById(id)
                .orElseThrow(() -> new BusinessException(CouponErrorCode.NOT_FOUND_COUPON));
    }

    @Override
    public List<IssuedCoupon> findByUserId(Long userId) {
        return jpa.findAllByUserId(userId);
    }
}
