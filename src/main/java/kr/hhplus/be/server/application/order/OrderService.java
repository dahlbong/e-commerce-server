package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.api.order.OrderRequest;
import kr.hhplus.be.server.domain.BusinessException;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.Payment;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.PaymentRepository;
import kr.hhplus.be.server.application.product.ProductService;
import kr.hhplus.be.server.application.point.PointService;
import kr.hhplus.be.server.domain.order.OrderEventSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ProductService productService;
    private final PointService pointService;
    private final OrderEventSender orderEventSender;

    @Transactional
    public List<Order> placeOrder(OrderRequest request) {
        // 1. 상품 가격 조회
        List<BigDecimal> prices = request.getItems().stream()
                .map(item -> productService.getById(item.productId()).getPrice())
                .toList();

        // 2. 주문 도메인 생성 (단가 포함)
        List<Order> orders = request.toOrders(prices);

        // 3. 총 결제 금액 계산
        BigDecimal totalAmount = orders.stream()
                .map(Order::calculateTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. 결제 도메인 생성
        Payment payment = request.toPayment(totalAmount);

        // 5. 포인트 차감 및 상태 전이
        try {
            pointService.use(request.getUserId(), totalAmount);
            payment.markAsCompleted();
            orders.forEach(Order::markAsCompleted);
        } catch (BusinessException e) {
            payment.markAsFailed();
            orders.forEach(Order::markAsFailed);
            throw e;
        }

        // 6. 연관관계 설정 및 저장
        for (Order order : orders) {
            order.setPayment(payment);
            orderRepository.save(order);
        }
        paymentRepository.save(payment);

        // 7. 외부 이벤트 전송
        orderEventSender.send(orders);

        return orders;
    }

    public Order getOrder(Long id) {
        return orderRepository.findById(id);
    }
}
