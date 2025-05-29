package kr.hhplus.be.server.infra.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.order.event.OrderPayEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-payment", groupId = "order-payment-group")
    public void handleOrderPaymentMessage(
            @Payload OrderPayEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        try {
            log.info("Kafka 메시지 수신: topic={}, partition={}, offset={}", topic, partition, offset);
            log.info("메시지 내용: {}", event);
            acknowledgment.acknowledge();

            // 비즈니스 로직 처리
            processOrderPaymentEvent(event);

            // 수동 커밋
            acknowledgment.acknowledge();
            log.info("Order payment message processed successfully");

        } catch (Exception e) {
            log.error("Error processing order payment message: {}", e.getMessage(), e);
            // 에러 처리 로직 (재시도, DLQ 등)
        }
    }

    @KafkaListener(topics = "user-coupon", groupId = "user-coupon-group")
    public void handleUserCouponMessage(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        try {
            log.info("주문 토픽 메시지 메시지 수신: {}, partition: {}, offset: {}",
                    topic, partition, offset);

            // 비즈니스 로직 처리
            processUserCouponEvent(message);

            // 수동 커밋
            acknowledgment.acknowledge();
            log.info("유저 쿠폰 메시지 발행 성공");

        } catch (Exception e) {
            log.error("유저 쿠폰 메시지 발행 실패: {}", e.getMessage(), e);
        }
    }

    public void processOrderPaymentEvent(OrderPayEvent event) {
        log.info("주문 결제 이벤트 처리 : {}, user: {}, amount: {}",
                event.getOrderId(), event.getUserId(), event.getTotalPrice());

        // 외부 시스템 연동, 알림 발송 등의 비즈니스 로직
    }

    public void processUserCouponEvent(String message) {
        log.info("사용자 쿠폰 이벤트 처리 : {}", message);

        // 쿠폰 발급, 쿠폰 사용 등의 비즈니스 로직
    }
}