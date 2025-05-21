package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.point.PointCommand;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.payment.PaymentCommand;
import kr.hhplus.be.server.domain.product.ProductCommand;
import kr.hhplus.be.server.domain.product.ProductInfo;
import kr.hhplus.be.server.domain.rank.RankCommand;
import kr.hhplus.be.server.domain.stock.StockCommand;
import kr.hhplus.be.server.domain.user.userCoupon.UserCouponCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderCriteria {

    @Getter
    public static class OrderPayment {

        private final Long userId;
        private final Long couponId;
        private final List<OrderProduct> products;

        private OrderPayment(Long userId, Long couponId, List<OrderProduct> products) {
            this.userId = userId;
            this.couponId = couponId;
            this.products = products;
        }

        public static OrderPayment of(Long userId, Long couponId, List<OrderProduct> products) {
            return new OrderPayment(userId, couponId, products);
        }

        public ProductCommand.OrderProducts toProductCommand() {
            return ProductCommand.OrderProducts.of(
                    products.stream()
                            .map(o -> ProductCommand.OrderProduct.of(o.getProductId(), o.getQuantity()))
                            .toList()
            );
        }

        public OrderCommand.Create toOrderCommand(Long userCouponId, double discountRage, ProductInfo.OrderProducts productInfo) {
            List<OrderCommand.OrderProduct> orderProducts = productInfo.getProducts().stream()
                    .map(p -> OrderCommand.OrderProduct.builder()
                            .productId(p.getProductId())
                            .productName(p.getProductName())
                            .productPrice(p.getProductPrice())
                            .quantity(p.getQuantity())
                            .build()
                    ).toList();

            return OrderCommand.Create.of(userId, userCouponId, discountRage, orderProducts);
        }

        public UserCouponCommand.UsableCoupon toCouponCommand() {
            return UserCouponCommand.UsableCoupon.of(userId, couponId);
        }

        public PointCommand.Use toPointCommand(long totalPrice) {
            return PointCommand.Use.of(userId, totalPrice);
        }

        public StockCommand.OrderProducts toStockCommand() {
            return StockCommand.OrderProducts.of(
                    products.stream()
                            .map(o -> StockCommand.OrderProduct.of(o.getProductId(), o.getQuantity()))
                            .toList()
            );
        }

        public PaymentCommand.Payment toPaymentCommand(OrderInfo.Order order) {
            return PaymentCommand.Payment.of(order.getOrderId(), userId, order.getTotalPrice());
        }

        public RankCommand.CreateList toRankCommand(LocalDate date) {
            return RankCommand.CreateList.of(
                    products.stream()
                            .map(o -> RankCommand.Create.of(o.getProductId(), o.getQuantity(), date))
                            .toList()
            );
        }
    }

    @Getter
    public static class OrderProduct {

        private final Long productId;
        private final int quantity;

        private OrderProduct(Long productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public static OrderProduct of(Long productId, int quantity) {
            return new OrderProduct(productId, quantity);
        }
    }
}