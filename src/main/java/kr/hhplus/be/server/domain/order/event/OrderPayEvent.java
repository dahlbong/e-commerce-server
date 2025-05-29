package kr.hhplus.be.server.domain.order.event;

import kr.hhplus.be.server.domain.order.Order;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderPayEvent {

    private Long orderId;
    private Long userId;
    private long totalPrice;
    private long discountPrice;

    @Builder
    private OrderPayEvent(Long orderId, Long userId, long totalPrice, long discountPrice) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.discountPrice = discountPrice;
    }

    public static OrderPayEvent of(Order order) {
        return OrderPayEvent.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .totalPrice(order.getTotalPrice())
                .discountPrice(order.getDiscountPrice())
                .build();
    }
}