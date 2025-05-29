package kr.hhplus.be.server.infra.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(String topic, String key, Object message) {
        log.info("메시지 전송: {}, key: {}, message: {}", topic, key, message);

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, message);

        future.whenComplete((result, exception) -> {
            if (exception == null) {
                log.info("메시지 전송 성공: {}, partition: {}, offset: {}",
                        topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            } else {
                log.error("메시지 전송 실패: {}, error: {}", topic, exception.getMessage(), exception);
            }
        });
    }

    public void sendMessage(String topic, Object message) {
        sendMessage(topic, null, message);
    }

    public void sendOrderPaymentMessage(Object message) {
        sendMessage("order-payment", message);
    }

    public void sendUserCouponMessage(Object message) {
        sendMessage("user-coupon", message);
    }
}