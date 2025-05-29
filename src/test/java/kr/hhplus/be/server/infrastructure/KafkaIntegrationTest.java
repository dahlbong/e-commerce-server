package kr.hhplus.be.server.infrastructure;

import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.infra.kafka.KafkaProducer;
import kr.hhplus.be.server.supporters.container.KafkaContainerExtension;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@ExtendWith(KafkaContainerExtension.class)
@EmbeddedKafka(partitions = 3, topics = {"order-payment"}, brokerProperties = {
        "listeners=PLAINTEXT://localhost:9092",
        "port=9092"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KafkaIntegrationTest {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private OrderService orderService;

    private KafkaConsumer<String, String> testConsumer;

    @BeforeAll
    void setUp() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        testConsumer = new KafkaConsumer<>(props);
        testConsumer.subscribe(List.of("order-payment"));
        testConsumer.poll(Duration.ofMillis(100));
    }

    @AfterAll
    void tearDown() {
        if (testConsumer != null) {
            testConsumer.close();
        }
    }

    @Test
    @DisplayName("Kafka 메시지 전송 및 수신 통합 테스트")
    void testOrderPaymentMessage() throws Exception {
        // given
        String topic = "order-payment";
        String testMessage = "Order payment completed: orderId=100, userId=1, amount=5000";

        // when
        kafkaProducer.sendMessage(topic, null, testMessage);
        log.info("Kafka 메시지 전송 완료: {}", testMessage);

        boolean messageReceived = false;
        String receivedMessage = null;
        long endTime = System.currentTimeMillis() + 10000;

        Thread.sleep(1000); // 리밸런싱 대기

        while (System.currentTimeMillis() < endTime && !messageReceived) {
            ConsumerRecords<String, String> records = testConsumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> record : records) {
                log.info("Kafka 메시지 수신: {}", record.value());
                if (topic.equals(record.topic())) {
                    receivedMessage = record.value();
                    messageReceived = true;
                    break;
                }
            }
        }

        // then
        assertThat(messageReceived).isTrue();
        assertThat(receivedMessage).contains("Order payment completed");
        assertThat(receivedMessage).contains("orderId=100");
        assertThat(receivedMessage).contains("userId=1");
    }

    @Test
    @DisplayName("주문 생성 후 Kafka 메시지가 발행되는지 확인")
    @Transactional
    @Commit
    void testKafkaMessagePublishedAfterCommit() throws Exception {
        // given
        long userId = 1L;
        long productId = 10L;
        long price = 1000L;
        long couponId = 1L;

        OrderCommand.OrderProduct commandProduct = OrderCommand.OrderProduct.of(
                productId, "상품1", price, 1);
        OrderCommand.Create command = OrderCommand.Create.of(
                userId, couponId, 0.0, List.of(commandProduct));

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> receivedMessage = new AtomicReference<>();

        Thread consumerThread = new Thread(() -> {
            try {
                long endTime = System.currentTimeMillis() + 30000;
                while (System.currentTimeMillis() < endTime) {
                    ConsumerRecords<String, String> records = testConsumer.poll(Duration.ofMillis(1000));
                    for (ConsumerRecord<String, String> record : records) {
                        log.info("Kafka 메시지 수신: topic={}, value={}", record.topic(), record.value());
                        // Order payment completed 메시지 중에서 해당 userId를 포함한 메시지 찾기
                        if (record.value().contains("Order payment completed") &&
                                record.value().contains("userId=" + userId)) {
                            receivedMessage.set(record.value());
                            latch.countDown();
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Consumer thread error", e);
            }
        });

        log.info("=== 테스트 시작 ===");
        consumerThread.start();

        // when
        OrderInfo.Order result = orderService.createOrder(command);
        log.info("주문 생성 완료, 비동기 이벤트 처리 대기");

        // then
        boolean messageReceived = latch.await(30, TimeUnit.SECONDS);
        consumerThread.interrupt();

        log.info("=== 테스트 결과 ===");
        if (receivedMessage.get() != null) {
            log.info("수신된 메시지: {}", receivedMessage.get());
        }

        assertThat(messageReceived).isTrue();
        assertThat(receivedMessage.get()).contains("Order payment completed");
        assertThat(receivedMessage.get()).contains("userId=" + userId);
    }
}