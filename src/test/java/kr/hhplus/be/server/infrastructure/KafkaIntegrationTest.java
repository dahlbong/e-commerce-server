package kr.hhplus.be.server.infrastructure;

import kr.hhplus.be.server.infra.kafka.KafkaProducer;
import kr.hhplus.be.server.supporters.container.KafkaContainerExtension;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.time.Duration;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@ExtendWith(KafkaContainerExtension.class)
@EmbeddedKafka(partitions = 3, topics = {"user-coupon"}, brokerProperties = {
        "listeners=PLAINTEXT://localhost:9092",
        "port=9092"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KafkaIntegrationTest {

    @Autowired
    private KafkaProducer kafkaProducer;

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
        TopicPartition partition = new TopicPartition("user-coupon", 0);
        testConsumer.assign(List.of(partition));
        testConsumer.seekToBeginning(List.of(partition));
    }

    @AfterAll
    void tearDown() {
        if (testConsumer != null) {
            testConsumer.close();
        }
    }

    @Test
    @DisplayName("Kafka 메시지 전송 및 수신 통합 테스트")
    void testUserCouponMessage() throws Exception {
        // given
        String topic = "user-coupon";
        String testMessage = "User coupon published: userId=100, couponId=1";

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
        assertThat(receivedMessage).contains("userId=100");
        assertThat(receivedMessage).contains("couponId=1");
    }
}
