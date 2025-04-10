package kr.hhplus.be.server.api.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.Payment;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;

@Getter
public class OrderRequest {

    private Long userId;
    private List<OrderItemRequest> items;
    private String paymentMethod;

    public static OrderRequest of(Long userId, List<OrderItemRequest> items, String method) {
        return new OrderRequest(userId, items, method);
    }

    public OrderRequest(Long userId, List<OrderItemRequest> items, String method) {
        this.userId = userId;
        this.items = items;
        this.paymentMethod = method;
    }

    public List<Order> toOrders(List<BigDecimal> prices) {
        return IntStream.range(0, items.size())
                .mapToObj(i -> {
                    OrderItemRequest item = items.get(i);
                    BigDecimal price = prices.get(i);
                    return Order.of(userId, item.productId(), price, item.quantity());
                })
                .toList();
    }

    public Payment toPayment(BigDecimal amount) {
        return Payment.of(paymentMethod, amount);
    }

    public record OrderItemRequest(Long productId, int quantity) {}
}
