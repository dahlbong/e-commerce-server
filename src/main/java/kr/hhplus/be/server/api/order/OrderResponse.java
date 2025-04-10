package kr.hhplus.be.server.api.order;

import kr.hhplus.be.server.domain.order.Order;
import lombok.Getter;

@Getter
public class OrderResponse {

    private final Long orderId;
    private final Long userId;
    private final Long productId;
    private final int quantity;

    public OrderResponse(Long orderId, Long userId, Long productId, int quantity) {
        this.orderId = orderId;
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getProductId(),
                order.getQuantity()
        );
    }
}
