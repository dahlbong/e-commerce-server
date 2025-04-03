package kr.hhplus.be.server.api.controller;


import kr.hhplus.be.server.api.dto.OrderRequest;
import kr.hhplus.be.server.api.dto.OrderResponse;
import kr.hhplus.be.server.domain.model.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    // 더미 주문 저장소: orderId를 키로 주문 정보를 저장
    private static Map<String, Order> orderStore = new HashMap<>();

    // 주문 생성 및 결제 처리 (Mock 구현)
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        // 간단한 더미 주문 번호 생성 및 총 주문금액 계산 (실제 로직에서는 재고검증, 할인적용 등 필요)
        String orderId = "ORD" + (orderStore.size() + 1);
        double totalAmount = 100.0; // 더미 총액
        Order order = new Order(orderId, request.getUserId(), totalAmount, "COMPLETED", new Date().toString());
        orderStore.put(orderId, order);

        OrderResponse response = new OrderResponse(order.getOrderId(), order.getStatus(), order.getUserId(), order.getTotalAmount());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 주문 상세 조회 API
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderId) {
        Order order = orderStore.get(orderId);
        if(order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        OrderResponse response = new OrderResponse(order.getOrderId(), order.getStatus(), order.getUserId(), order.getTotalAmount());
        return ResponseEntity.ok(response);
    }
}