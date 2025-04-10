package kr.hhplus.be.server.api.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.enums.OrderStatus;

import java.math.BigDecimal;

public record OrderResponse(
        Long orderId,
        Long userId,
        Long productId,
        int quantity,
        OrderStatus status,
        BigDecimal unitPrice,
        BigDecimal discountAmount,
        BigDecimal finalAmount
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getProductId(),
                order.getQuantity(),
                order.getStatus(),
                order.getUnitPrice(),
                order.getDiscountAmount(),
                order.calculateTotalPrice()
        );
    }
}
