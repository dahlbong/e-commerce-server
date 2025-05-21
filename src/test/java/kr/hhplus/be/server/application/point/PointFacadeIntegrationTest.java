package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.supporters.IntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class PointFacadeIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private PointFacade pointFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointRepository pointRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.create("항플");
        userRepository.save(user);

        Point point = Point.builder()
            .userId(user.getId())
            .amount(100_000L)
            .build();
        pointRepository.save(point);
    }

    @DisplayName("잔액을 충전한다.")
    @Test
    void chargePoint() {
        // given
        PointCriteria.Charge criteria = PointCriteria.Charge.of(user.getId(), 10_000L);

        // when
        pointFacade.chargePoint(criteria);

        // then
        Point point = pointRepository.findOptionalByUserId(user.getId()).orElseThrow();
        assertThat(point.getAmount()).isEqualTo(110_000L);
    }

    @DisplayName("잔액을 조회한다.")
    @Test
    void getPoint() {
        // given
        Long userId = user.getId();

        // when
        PointResult.Point point = pointFacade.getPoint(userId);

        // then
        assertThat(point.getAmount()).isEqualTo(100_000L);
    }
}