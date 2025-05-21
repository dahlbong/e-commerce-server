package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.coupon.CouponInfo;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.product.ProductInfo;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.rank.RankService;
import kr.hhplus.be.server.domain.stock.StockService;
import kr.hhplus.be.server.domain.user.userCoupon.UserCouponInfo;
import kr.hhplus.be.server.domain.user.userCoupon.UserCouponService;
import kr.hhplus.be.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final UserService userService;
    private final ProductService productService;
    private final UserCouponService userCouponService;
    private final CouponService couponService;
    private final OrderService orderService;
    private final PointService pointService;
    private final StockService stockService;
    private final PaymentService paymentService;
    private final RankService rankService;

    private static final double NOT_DISCOUNT_RATE = 0.0;

    @Transactional
    public OrderResult.Order orderPayment(OrderCriteria.OrderPayment criteria) {
        userService.getUser(criteria.getUserId());

        ProductInfo.OrderProducts orderProducts = productService.getOrderProducts(criteria.toProductCommand());

        Optional<Long> optionalCouponId = Optional.ofNullable(criteria.getCouponId());
        Optional<UserCouponInfo.UsableCoupon> optionalUsableCoupon = optionalCouponId
                .map(id -> userCouponService.getUsableCoupon(criteria.toCouponCommand()));
        Optional<CouponInfo.Coupon> optionalCoupon = optionalCouponId
                .map(couponService::getCoupon);

        OrderCommand.Create orderCommand = criteria.toOrderCommand(
                optionalUsableCoupon.map(UserCouponInfo.UsableCoupon::getUserCouponId).orElse(null),
                optionalCoupon.map(CouponInfo.Coupon::getDiscountRate).orElse(NOT_DISCOUNT_RATE),
                orderProducts
        );
        OrderInfo.Order order = orderService.createOrder(orderCommand);

        pointService.usePoint(criteria.toPointCommand(order.getTotalPrice()));
        optionalUsableCoupon.ifPresent(coupon -> userCouponService.useUserCoupon(coupon.getUserCouponId()));
        stockService.deductStock(criteria.toStockCommand());
        paymentService.pay(criteria.toPaymentCommand(order));
        orderService.paidOrder(order.getOrderId());
        rankService.createSellRank(criteria.toRankCommand(LocalDate.now()));

        return OrderResult.Order.of(order);
    }
}