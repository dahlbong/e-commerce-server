package kr.hhplus.be.server.interfaces.point;

import io.restassured.http.ContentType;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.supporters.E2EControllerTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


class PointControllerE2ETest extends E2EControllerTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointRepository pointRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.create("항플");
        userRepository.save(user);
    }

    @DisplayName("잔액을 조회한다.")
    @Test
    void getPoint() {
        // given
        Point point = Point.create(user.getId());
        point.charge(100_000L);
        pointRepository.save(point);

        // when & then
        given()
        .when()
            .get("/api/v1/users/{id}/point", user.getId())
        .then()
            .log().all()
            .statusCode(HttpStatus.OK.value())
            .body("code", equalTo(200))
            .body("message", equalTo("OK"))
            .body("data.amount", equalTo(100_000));
    }

    @DisplayName("잔액이 없으면 조회 시 0원을 반환한다.")
    @Test
    void getPointWithoutPoint() {
        // when & then
        given()
            .when()
            .get("/api/v1/users/{id}/point", user.getId())
        .then()
            .log().all()
            .statusCode(HttpStatus.OK.value())
            .body("code", equalTo(200))
            .body("message", equalTo("OK"))
            .body("data.amount", equalTo(0));
    }

    @DisplayName("잔액 충전 시, 최대 금액을 초과할 수 없다.")
    @Test
    void chargePointWithOverMaxAmount() {
        // given
        Point point = Point.create(user.getId());
        point.charge(10_000_000L);
        pointRepository.save(point);

        PointRequest.Charge request = PointRequest.Charge.of(1L);

        // when & then
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1/users/{id}/point/charge", user.getId())
        .then()
            .log().all()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("code", equalTo(400))
            .body("message", equalTo("최대 금액을 초과할 수 없습니다."));
    }

    @DisplayName("잔액을 충전한다.")
    @Test
    void chargePoint() {
        // given
        Point point = Point.create(user.getId());
        point.charge(10_000L);
        pointRepository.save(point);

        PointRequest.Charge request = PointRequest.Charge.of(1_000_000L);

        // when & then
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1/users/{id}/point/charge", user.getId())
        .then()
            .log().all()
            .statusCode(HttpStatus.OK.value())
            .body("code", equalTo(200))
            .body("message", equalTo("OK"));
    }
}
