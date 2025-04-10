package kr.hhplus.be.server.infra.coupon;

import kr.hhplus.be.server.domain.BusinessException;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.enums.CouponErrorCode;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponJpaRepository jpa;

    @Override
    public Coupon findById(Long id) {
        return jpa.findById(id)
                .orElseThrow(() -> new BusinessException(CouponErrorCode.NOT_FOUND_COUPON));
    }

    @Override
    public void save(Coupon coupon) {
        jpa.save(coupon);
    }
}
