package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.point.PointService;
import kr.hhplus.be.server.application.product.ProductService;
import kr.hhplus.be.server.application.user.UserService;
import kr.hhplus.be.server.domain.BusinessException;
import kr.hhplus.be.server.domain.coupon.IssuedCoupon;
import kr.hhplus.be.server.domain.coupon.IssuedCouponRepository;
import kr.hhplus.be.server.domain.coupon.enums.CouponErrorCode;
import kr.hhplus.be.server.domain.order.*;
import kr.hhplus.be.server.api.order.OrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductService productService;
    private final PointService pointService;
    private final UserService userService;

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final IssuedCouponRepository issuedCouponRepository;

    private final OrderEventSender orderEventSender;

    public List<Order> placeOrder(OrderRequest request) {
        Long userId = request.userId();
        userService.getOrCreateById(userId);

        List<Order> orders = request.toOrders(productService);

        BigDecimal totalAmount = orders.stream()
                .map(Order::calculateTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 할인 적용
        BigDecimal discountAmount;
        if (request.issuedCouponId() != null) {
            IssuedCoupon coupon = issuedCouponRepository.findById(request.issuedCouponId());
            if (!coupon.isValidNow(LocalDateTime.now())) {
                throw new BusinessException(CouponErrorCode.COUPON_INACTIVATED);
            }
            discountAmount = calculateDiscountAmount(totalAmount, coupon);
            coupon.use();
            issuedCouponRepository.save(coupon);

            // 주문마다 할인 적용
            orders.forEach(o -> o.applyDiscount(discountAmount.divide(BigDecimal.valueOf(orders.size()), RoundingMode.DOWN)));
        } else {
            discountAmount = BigDecimal.ZERO;
        }

        BigDecimal finalAmount = totalAmount.subtract(discountAmount);

        // 포인트 차감
        pointService.use(userId, finalAmount);

        // 결제 객체 생성
        Payment payment = Payment.of("POINT", finalAmount);
        orders.forEach(order -> order.setPayment(payment));

        payment.markAsCompleted();
        orders.forEach(Order::markAsCompleted);

        orders.forEach(orderRepository::save);
        paymentRepository.save(payment);
        orderEventSender.send(orders);

        return orders;
    }

    private BigDecimal calculateDiscountAmount(BigDecimal totalAmount, IssuedCoupon coupon) {
        return switch (coupon.getDiscountType()) {
            case FIXED_AMOUNT -> coupon.getDiscountAmount();
            case PERCENTAGE -> totalAmount
                    .multiply(coupon.getDiscountAmount())
                    .divide(BigDecimal.valueOf(100), RoundingMode.DOWN);
        };
    }
}
