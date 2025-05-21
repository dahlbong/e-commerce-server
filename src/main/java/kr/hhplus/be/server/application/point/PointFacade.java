package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.domain.point.PointInfo;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.domain.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointFacade {

    private final UserService userService;
    private final PointService balanceService;

    @Transactional
    public void chargePoint(PointCriteria.Charge criteria) {
        userService.getUser(criteria.getUserId());
        balanceService.chargePoint(criteria.toCommand());
    }

    @Transactional(readOnly = true)
    public PointResult.Point getPoint(Long userId) {
        userService.getUser(userId);
        PointInfo.Point balance = balanceService.getPoint(userId);
        return PointResult.Point.of(balance.getAmount());
    }
}