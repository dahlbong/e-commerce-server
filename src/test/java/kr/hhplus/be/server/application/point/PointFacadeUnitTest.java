package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.domain.point.PointInfo;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.supporters.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PointFacadeUnitTest extends MockTestSupport {

    @InjectMocks
    private PointFacade balanceFacade;

    @Mock
    private UserService userService;

    @Mock
    private PointService balanceService;

    @DisplayName("잔액을 충전한다.")
    @Test
    void chargePoint() {
        // given
        PointCriteria.Charge criteria = mock(PointCriteria.Charge.class);

        // when
        balanceFacade.chargePoint(criteria);

        // then
        InOrder inOrder = inOrder(userService, balanceService);
        inOrder.verify(userService, times(1)).getUser(criteria.getUserId());
        inOrder.verify(balanceService, times(1)).chargePoint(criteria.toCommand());
    }

    @DisplayName("잔액을 조회한다.")
    @Test
    void getPoint() {
        // given
        Long userId = anyLong();

        when(balanceService.getPoint(userId))
            .thenReturn(PointInfo.Point.of(10_000L));

        // when
        PointResult.Point balance = balanceFacade.getPoint(userId);

        // then
        InOrder inOrder = inOrder(userService, balanceService);
        inOrder.verify(userService, times(1)).getUser(userId);
        inOrder.verify(balanceService, times(1)).getPoint(userId);
        assertThat(balance.getAmount()).isEqualTo(10_000L);
    }
}