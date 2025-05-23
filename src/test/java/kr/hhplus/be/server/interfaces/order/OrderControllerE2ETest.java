package kr.hhplus.be.server.interfaces.order;

import io.restassured.http.ContentType;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.enums.CouponStatus;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.enums.SellingStatus;
import kr.hhplus.be.server.domain.stock.Stock;
import kr.hhplus.be.server.domain.stock.StockRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.domain.user.enums.UserCouponUsedStatus;
import kr.hhplus.be.server.domain.user.userCoupon.UserCoupon;
import kr.hhplus.be.server.domain.user.userCoupon.UserCouponRepository;
import kr.hhplus.be.server.supporters.E2EControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class OrderControllerE2ETest extends E2EControllerTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private CouponRepository couponRepository;

    @DisplayName("주문/결제 시, 잔액은 충분해야 한다.")
    @Test
    void orderPaymentWithInsufficientPoint() {
        // given
        User user = User.create("항플");
        userRepository.save(user);

        Point point = Point.create(user.getId());
        pointRepository.save(point);

        Product product1 = Product.create("항해 블랙뱃지", 100_000L, SellingStatus.SELLING);
        Product product2 = Product.create("항해 화이트뱃지", 200_000L, SellingStatus.SELLING);
        productRepository.save(product1);
        productRepository.save(product2);

        Stock stock1 = Stock.create(product1.getId(), 100);
        Stock stock2 = Stock.create(product2.getId(), 200);
        stockRepository.save(stock1);
        stockRepository.save(stock2);

        OrderRequest.OrderPayment request = OrderRequest.OrderPayment.of(
            user.getId(),
            null,
            List.of(
                OrderRequest.OrderProduct.of(product1.getId(), 1),
                OrderRequest.OrderProduct.of(product2.getId(), 2)
            )
        );

        // when & then
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1/orders")
            .then()
            .log().all()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("code", equalTo(400))
            .body("message", equalTo("잔액이 부족합니다."));
    }

    @DisplayName("주문/결제 시, 재고는 충분해야 한다.")
    @Test
    void orderPaymentWithInsufficientStock() {
        // given
        User user = User.create("항플");
        userRepository.save(user);

        Point point = Point.create(user.getId());
        point.charge(1_000_000L);
        pointRepository.save(point);

        Product product1 = Product.create("항해 블랙뱃지", 100_000L, SellingStatus.SELLING);
        Product product2 = Product.create("항해 화이트뱃지", 200_000L, SellingStatus.SELLING);
        productRepository.save(product1);
        productRepository.save(product2);

        Stock stock1 = Stock.create(product1.getId(), 0);
        Stock stock2 = Stock.create(product2.getId(), 0);
        stockRepository.save(stock1);
        stockRepository.save(stock2);

        OrderRequest.OrderPayment request = OrderRequest.OrderPayment.of(
            user.getId(),
            null,
            List.of(
                OrderRequest.OrderProduct.of(product1.getId(), 1),
                OrderRequest.OrderProduct.of(product2.getId(), 2)
            )
        );

        // when & then
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1/orders")
            .then()
            .log().all()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("code", equalTo(400))
            .body("message", equalTo("재고가 부족합니다."));
    }

    @DisplayName("주문/결제 시, 쿠폰은 사용 가능해야 한다.")
    @Test
    void orderPaymentWithInvalidCoupon() {
        // given
        User user = User.create("항플");
        userRepository.save(user);

        Coupon coupon = Coupon.create("쿠폰명1", 0.1, 10, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));
        couponRepository.save(coupon);

        UserCoupon userCoupon = UserCoupon.builder()
            .userId(user.getId())
            .couponId(coupon.getId())
            .usedStatus(UserCouponUsedStatus.USED)
            .build();

        userCouponRepository.save(userCoupon);

        Point point = Point.create(user.getId());
        point.charge(1_000_000L);
        pointRepository.save(point);

        Product product1 = Product.create("항해 블랙뱃지", 100_000L, SellingStatus.SELLING);
        productRepository.save(product1);

        Stock stock1 = Stock.create(product1.getId(), 100);
        stockRepository.save(stock1);

        OrderRequest.OrderPayment request = OrderRequest.OrderPayment.of(
            user.getId(),
            coupon.getId(),
            List.of(
                OrderRequest.OrderProduct.of(product1.getId(), 1)
            )
        );

        // when & then
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1/orders")
            .then()
            .log().all()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .body("code", equalTo(500))
            .body("message", equalTo("사용할 수 없는 쿠폰입니다."));
    }

    @DisplayName("주문/결제 시, 주문 상품은 판매 중이어야 한다.")
    @Test
    void orderPaymentWithInvalidProduct() {
        // given
        User user = User.create("항플");
        userRepository.save(user);

        Point point = Point.create(user.getId());
        point.charge(1_000_000L);
        pointRepository.save(point);

        Product product1 = Product.create("항해 블랙뱃지", 100_000L, SellingStatus.HOLD);
        Product product2 = Product.create("항해 화이트뱃지", 200_000L, SellingStatus.SELLING);
        productRepository.save(product1);
        productRepository.save(product2);

        Stock stock1 = Stock.create(product1.getId(), 100);
        Stock stock2 = Stock.create(product2.getId(), 200);
        stockRepository.save(stock1);
        stockRepository.save(stock2);

        OrderRequest.OrderPayment request = OrderRequest.OrderPayment.of(
            user.getId(),
            null,
            List.of(
                OrderRequest.OrderProduct.of(product1.getId(), 1),
                OrderRequest.OrderProduct.of(product2.getId(), 2)
            )
        );

        // when & then
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1/orders")
        .then()
            .log().all()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .body("code", equalTo(500))
            .body("message", equalTo("주문 불가한 상품이 포함되어 있습니다."));
    }

    @DisplayName("주문/결제 한다.")
    @Test
    void orderPayment() {
        // given
        User user = User.create("항플");
        userRepository.save(user);

        Point point = Point.create(user.getId());
        point.charge(1_000_000L);
        pointRepository.save(point);

        Product product1 = Product.create("항해 블랙뱃지", 100_000L, SellingStatus.SELLING);
        Product product2 = Product.create("항해 화이트뱃지", 200_000L, SellingStatus.SELLING);
        productRepository.save(product1);
        productRepository.save(product2);

        Stock stock1 = Stock.create(product1.getId(), 100);
        Stock stock2 = Stock.create(product2.getId(), 200);
        stockRepository.save(stock1);
        stockRepository.save(stock2);

        OrderRequest.OrderPayment request = OrderRequest.OrderPayment.of(
            user.getId(),
            null,
            List.of(
                OrderRequest.OrderProduct.of(product1.getId(), 1),
                OrderRequest.OrderProduct.of(product2.getId(), 2)
            )
        );

        // when & then
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1/orders")
        .then()
            .log().all()
            .statusCode(HttpStatus.OK.value())
            .body("code", equalTo(200))
            .body("message", equalTo("OK"));
    }
}