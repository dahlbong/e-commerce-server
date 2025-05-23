package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderExternalClient orderExternalClient;

    public OrderInfo.Order createOrder(OrderCommand.Create command) {
        List<OrderProduct> orderProducts = command.getProducts().stream()
                .map(this::createOrderProduct)
                .toList();

        Order order = Order.create(command.getUserId(), command.getUserCouponId(), command.getDiscountRate(), orderProducts);
        orderRepository.save(order);

        return OrderInfo.Order.of(order.getId(), order.getTotalPrice(), order.getDiscountPrice());
    }

    public void paidOrder(Long orderId) {
        Order order = orderRepository.findById(orderId);
        order.paid(LocalDateTime.now());

        orderExternalClient.sendOrderMessage(order);
    }

    public OrderInfo.PaidProducts getPaidProducts(OrderCommand.DateQuery command) {
        OrderCommand.PaidProducts queryCommand = command.toPaidProductsQuery(OrderStatus.PAID);
        List<OrderInfo.PaidProduct> paidProducts = orderRepository.findPaidProducts(queryCommand);

        return OrderInfo.PaidProducts.of(paidProducts);
    }

    private OrderProduct createOrderProduct(OrderCommand.OrderProduct product) {
        return OrderProduct.create(
                product.getProductId(),
                product.getProductName(),
                product.getProductPrice(),
                product.getQuantity()
        );
    }
}