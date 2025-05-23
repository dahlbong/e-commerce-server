package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.enums.CouponStatus;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.enums.OrderStatus;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.enums.SellingStatus;
import kr.hhplus.be.server.domain.rank.*;
import kr.hhplus.be.server.domain.rank.enums.RankType;
import kr.hhplus.be.server.domain.stock.Stock;
import kr.hhplus.be.server.domain.stock.StockRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.domain.user.enums.UserCouponUsedStatus;
import kr.hhplus.be.server.domain.user.userCoupon.UserCoupon;
import kr.hhplus.be.server.domain.user.userCoupon.UserCouponRepository;
import kr.hhplus.be.server.supporters.IntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class OrderFacadeIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private RankRepository rankRepository;

    private User user;

    private Product product;

    @BeforeEach
    void setUp() {
        user = User.create("항플");
        userRepository.save(user);

        Point point = Point.builder()
            .userId(user.getId())
            .amount(500_000L)
            .build();

        pointRepository.save(point);

        product = Product.create("항플 블랙 뱃지", 100_000L, SellingStatus.SELLING);
        productRepository.save(product);

        Stock stock = Stock.create(product.getId(), 100);
        stockRepository.save(stock);
    }

    @DisplayName("쿠폰 없이 주문 결제를 한다.")
    @Test
    void orderPaymentWithoutCoupon() {
        // given
        OrderCriteria.OrderPayment criteria = OrderCriteria.OrderPayment.of(user.getId(), null,
            List.of(OrderCriteria.OrderProduct.of(product.getId(), 2))
        );

        // when
        OrderResult.Order result = orderFacade.orderPayment(criteria);

        // then
        Point point = pointRepository.findOptionalByUserId(user.getId()).orElseThrow();
        assertThat(point.getAmount()).isEqualTo(300_000L);

        Stock stock = stockRepository.findByProductId(product.getId());
        assertThat(stock.getQuantity()).isEqualTo(98);

        Order order = orderRepository.findById(result.getOrderId());
        assertThat(order.getTotalPrice()).isEqualTo(200_000L);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);

        RankCommand.Query command = RankCommand.Query.of(
            1,
            RankKey.ofDays(RankType.SELL, 3),
            RankKeys.ofDaysWithDate(RankType.SELL, 3, LocalDate.now())
        );
        List<RankInfo.PopularProduct> popularSellRanks = rankRepository.findPopularSellRanks(command);
        assertThat(popularSellRanks).hasSize(1)
            .extracting(RankInfo.PopularProduct::getProductId)
            .containsExactly(product.getId());
    }

    @DisplayName("쿠폰이 있는 주문 결제를 한다.")
    @Test
    void orderPaymentWithCoupon() {
        // given
        Coupon coupon = Coupon.create("쿠폰명", 0.1, 10, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));
        couponRepository.save(coupon);

        userCouponRepository.save(UserCoupon.create(user.getId(), coupon.getId()));

        OrderCriteria.OrderPayment criteria = OrderCriteria.OrderPayment.of(user.getId(), coupon.getId(),
            List.of(OrderCriteria.OrderProduct.of(product.getId(), 2))
        );

        // when
        OrderResult.Order result = orderFacade.orderPayment(criteria);

        // then
        Point point = pointRepository.findOptionalByUserId(user.getId()).orElseThrow();
        assertThat(point.getAmount()).isEqualTo(320_000L);

        UserCoupon userCoupon = userCouponRepository.findByUserIdAndCouponId(user.getId(), coupon.getId());
        assertThat(userCoupon.getUsedAt()).isNotNull();
        assertThat(userCoupon.getUsedStatus()).isEqualTo(UserCouponUsedStatus.USED);

        Stock stock = stockRepository.findByProductId(product.getId());
        assertThat(stock.getQuantity()).isEqualTo(98);

        Order order = orderRepository.findById(result.getOrderId());
        assertThat(order.getTotalPrice()).isEqualTo(180_000L);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);

        RankCommand.Query command = RankCommand.Query.of(
            1,
            RankKey.ofDays(RankType.SELL, 3),
            RankKeys.ofDaysWithDate(RankType.SELL, 3, LocalDate.now())
        );
        List<RankInfo.PopularProduct> popularSellRanks = rankRepository.findPopularSellRanks(command);
        assertThat(popularSellRanks).hasSize(1)
            .extracting(RankInfo.PopularProduct::getProductId)
            .containsExactly(product.getId());
    }
}