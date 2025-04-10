package kr.hhplus.be.server.api.order;

import kr.hhplus.be.server.application.product.ProductService;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.product.Product;

import java.util.List;

public record OrderRequest(
        Long userId,
        List<OrderItemRequest> items,
        String paymentMethod, // "POINT" 고정
        Long issuedCouponId
) {
    public List<Order> toOrders(ProductService productService) {
        return items.stream()
                .map(item -> {
                    Product product = productService.getById(item.productId());
                    return Order.of(userId, product, item.quantity());
                })
                .toList();
    }

    public record OrderItemRequest(Long productId, int quantity) {}
}
