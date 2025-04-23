package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.Order;

import java.util.List;

public interface OrderEventSender {
    void send(List<Order> orders);
}
