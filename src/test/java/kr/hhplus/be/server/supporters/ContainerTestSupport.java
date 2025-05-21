package kr.hhplus.be.server.supporters;

import kr.hhplus.be.server.supporters.container.MysqlContainerExtension;
import kr.hhplus.be.server.supporters.container.RedisContainerExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;

@ExtendWith({
    MysqlContainerExtension.class,
    RedisContainerExtension.class,
})
public abstract class ContainerTestSupport {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // MySQL
        MySQLContainer<?> mySQLContainer = MysqlContainerExtension.getContainer();
        registry.add("spring.datasource.url", () -> mySQLContainer.getJdbcUrl() + "?characterEncoding=UTF-8&serverTimezone=UTC");
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);

        // Redis
        GenericContainer<?> redisContainer = RedisContainerExtension.getContainer();
        registry.add("spring.redis.host", redisContainer::getHost);
        registry.add("spring.redis.port", redisContainer::getFirstMappedPort);
    }
}
