package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.coupon.CouponInfo;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.product.ProductInfo;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.rank.RankService;
import kr.hhplus.be.server.domain.stock.StockService;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.domain.user.userCoupon.UserCouponInfo;
import kr.hhplus.be.server.domain.user.userCoupon.UserCouponService;
import kr.hhplus.be.server.supporters.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

class OrderFacadeUnitTest extends MockTestSupport {

    @InjectMocks
    private OrderFacade orderFacade;

    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

    @Mock
    private UserCouponService userCouponService;

    @Mock
    private CouponService couponService;

    @Mock
    private OrderService orderService;

    @Mock
    private PointService pointService;

    @Mock
    private StockService stockService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private RankService rankService;

    @DisplayName("주문 결제를 한다.")
    @Test
    void orderPayment() {
        // given
        OrderCriteria.OrderPayment criteria = mock(OrderCriteria.OrderPayment.class);
        ProductInfo.OrderProducts mockOrderProducts = mock(ProductInfo.OrderProducts.class);
        UserCouponInfo.UsableCoupon mockUsableCoupon = mock(UserCouponInfo.UsableCoupon.class);
        CouponInfo.Coupon mockCoupon = mock(CouponInfo.Coupon.class);
        OrderInfo.Order mockOrder = mock(OrderInfo.Order.class);

        when(productService.getOrderProducts(any())).thenReturn(mockOrderProducts);
        when(userCouponService.getUsableCoupon(any())).thenReturn(mockUsableCoupon);
        when(couponService.getCoupon(any())).thenReturn(mockCoupon);
        when(orderService.createOrder(any())).thenReturn(mockOrder);

        // when
        orderFacade.orderPayment(criteria);

        // then
        InOrder inOrder = inOrder(userService,
            productService,
            userCouponService,
            couponService,
            orderService,
            orderService,
            pointService,
            stockService,
            paymentService,
            rankService
        );

        inOrder.verify(userService, times(1)).getUser(criteria.getUserId());
        inOrder.verify(productService, times(1)).getOrderProducts(criteria.toProductCommand());
        inOrder.verify(userCouponService, times(1)).getUsableCoupon(criteria.toCouponCommand());
        inOrder.verify(couponService, times(1)).getCoupon(criteria.getCouponId());
        inOrder.verify(orderService, times(1)).createOrder(criteria.toOrderCommand(
            mockUsableCoupon.getUserCouponId(),
            mockCoupon.getCouponId(),
            mockOrderProducts
        ));
        inOrder.verify(pointService, times(1)).usePoint(criteria.toPointCommand(
            mockOrder.getTotalPrice()
        ));
        inOrder.verify(userCouponService, times(1)).useUserCoupon(mockUsableCoupon.getUserCouponId());
        inOrder.verify(stockService, times(1)).deductStock(criteria.toStockCommand());
        inOrder.verify(paymentService, times(1)).pay(criteria.toPaymentCommand(mockOrder));
        inOrder.verify(orderService, times(1)).paidOrder(mockOrder.getOrderId());
        inOrder.verify(rankService, times(1)).createSellRank(criteria.toRankCommand(any()));
    }
}