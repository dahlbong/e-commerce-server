package kr.hhplus.be.server.supporters.container;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

public class KafkaContainerExtension implements BeforeAllCallback {

    private static final KafkaContainer kafkaContainer;

    static {
        DockerImageName imageName = DockerImageName.parse("apache/kafka:3.8.0");

        kafkaContainer = new KafkaContainer(imageName);
        kafkaContainer.start();

        // Spring의 Kafka 설정에 적용되도록 시스템 프로퍼티 설정
        System.setProperty("spring.kafka.bootstrap-servers", kafkaContainer.getBootstrapServers());
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        // 이미 static 블록에서 start() 실행됨
    }

    public static String getBootstrapServers() {
        return kafkaContainer.getBootstrapServers();
    }
}