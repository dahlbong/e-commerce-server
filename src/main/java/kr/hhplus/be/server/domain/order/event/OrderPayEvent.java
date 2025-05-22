package kr.hhplus.be.server.domain.order.event;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderEvent {

    private Long orderId;
    private Long userId;
    private long totalPrice;
    private long discountPrice;

    @Builder
    private OrderEvent(Long orderId, Long userId, long totalPrice, long discountPrice) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.discountPrice = discountPrice;
    }
}