package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.supporters.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class PointRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private PointRepository pointRepository;

    @DisplayName("잔액을 저장한다.")
    @Test
    void save() {
        // given
        Point point = Point.builder()
            .userId(1L)
            .amount(1_000L)
            .build();

        // when
        pointRepository.save(point);

        // then
        assertThat(point.getId()).isNotNull();
    }

    @DisplayName("잔액이 없는 유저의 잔액을 조회한다.")
    @Test
    void findOptionalByUserId() {
        // when
        Optional<Point> result = pointRepository.findOptionalByUserId(1L);

        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("잔액이 있는 유저의 잔액을 조회한다.")
    @Test
    void findByUserId() {
        // given
        Point point = Point.builder()
            .userId(1L)
            .amount(1_000L)
            .build();
        pointRepository.save(point);

        // when
        Point result = pointRepository.findOptionalByUserId(point.getUserId()).orElseThrow();

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getAmount()).isEqualTo(point.getAmount());
    }
}