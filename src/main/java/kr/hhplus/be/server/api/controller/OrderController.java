package kr.hhplus.be.server.api.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Order API", description = "주문 생성 및 조회 API")
public class OrderController {

    private static Map<String, Order> orderStore = new HashMap<>();

    @Operation(summary = "주문 생성", description = "주문 요청 정보를 기반으로 결제 처리 및 주문을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "주문 생성 성공"),
            @ApiResponse(responseCode = "400", description = "요청 데이터 오류",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"status\":400,\"message\":\"Invalid request\"}")))
    })
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        String orderId = "ORD" + (orderStore.size() + 1);
        double totalAmount = 100.0;
        Order order = new Order(orderId, request.getUserId(), totalAmount, "COMPLETED", new Date().toString());
        orderStore.put(orderId, order);

        OrderResponse response = new OrderResponse(order.getOrderId(), order.getStatus(), order.getUserId(), order.getTotalAmount());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "주문 상세 조회", description = "주문 ID로 주문 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 주문 ID",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"status\":404,\"message\":\"Order not found\"}")))
    })
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