package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderEventSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class OrderEventSenderImpl implements OrderEventSender {

    @Override
    public void send(List<Order> orders) {
        // 실제로는 Kafka, RabbitMQ, 외부 API 등으로 전송 가능
        log.info("[외부 전송] 주문 정보 전송됨: {}건", orders.size());
        orders.forEach(order -> log.info(">> Order ID={}, User={}, Product={}, Price={}",
                order.getId(), order.getUserId(), order.getProductId(), order.calculateTotalPrice()));
    }
}
