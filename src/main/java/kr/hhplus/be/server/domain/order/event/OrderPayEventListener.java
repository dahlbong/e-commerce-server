package kr.hhplus.be.server.domain.order.event;

import kr.hhplus.be.server.domain.order.OrderExternalClient;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.infra.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPayEventListener {

    private final OrderExternalClient orderExternalClient;
    private final OrderRepository orderRepository;
    private final KafkaProducer kafkaProducer;

    /**
     * 주문 완료 후 Kafka 메시지 전송 (사용자 쿠폰 이벤트)
     * - 트랜잭션 커밋 후 비동기로 실행
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderPayEvent(OrderPayEvent event) {
        try {
            log.info("=== Kafka 메시지 전송 시작 ===");
            log.info("주문 이벤트 Kafka 전송: orderId={}, userId={}", event.getOrderId(), event.getUserId());

            Thread.sleep(100);
            // order-payment 토픽으로 메시지 전송
            String message = String.format("Order payment completed: orderId=%d, userId=%d, amount=%d",
                    event.getOrderId(), event.getUserId(), event.getTotalPrice());

            log.info("Kafka 메시지 내용: {}", message);
            kafkaProducer.sendMessage("order-payment", null, message);
            log.info("Kafka 메시지 전송 완료");

        } catch (Exception e) {
            log.error("Kafka 메시지 전송 실패: orderId={}, error={}",
                    event.getOrderId(), e.getMessage(), e);
        }
    }

    /**
     * 결제 완료 알림톡 발송 확장 고려
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderEventForNotification(OrderPayEvent event) {
        try {
            log.info("결제 완료 알림톡 발송 시작: orderId={}", event.getOrderId());
            log.info("결제 완료 알림톡 발송 완료: orderId={}, userId={}, amount={}",
                    event.getOrderId(), event.getUserId(), event.getTotalPrice());

        } catch (Exception e) {
            log.error("결제 완료 알림톡 발송 실패: orderId={}, error={}",
                    event.getOrderId(), e.getMessage(), e);
        }
    }
}