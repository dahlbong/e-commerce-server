package kr.hhplus.be.server.api.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.application.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "주문/결제", description = "주문 요청 및 결제 API")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "주문 및 결제", description = "상품 ID, 수량, 쿠폰 ID를 포함한 주문 요청을 통해 결제를 수행합니다.")
    @PostMapping
    public List<OrderResponse> placeOrder(@RequestBody OrderRequest request) {
        return orderService.placeOrder(request).stream()
                .map(OrderResponse::from)
                .toList();
    }

}
