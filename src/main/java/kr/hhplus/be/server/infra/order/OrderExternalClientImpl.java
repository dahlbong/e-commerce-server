package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderExternalClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class OrderExternalClientImpl implements OrderExternalClient {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void sendOrderMessage(Order order) {
        try {
            String orderData = createOrderDataForPlatform(order);

            // TODO: 실제 외부 API 호출 구현
            // HttpClient를 사용한 REST API 호출 or 메시지 큐를 통한 메시지 전송 구현 필요

            log.info("=== 외부 데이터 플랫폼 전송 성공 ===");
            log.info("전송 시각: {}", LocalDateTime.now().format(FORMATTER));
            log.info("주문 정보: {}", orderData);
            log.info("========================================");

        } catch (Exception e) {
            log.error("외부 데이터 플랫폼 전송 실패: orderId={}, error={}", order.getId(), e.getMessage());
            throw e;
        }
    }

    private String createOrderDataForPlatform(Order order) {
        return String.format(
                "{"
                        + "\"orderId\":%d,"
                        + "\"userId\":%d,"
                        + "\"totalPrice\":%d,"
                        + "\"discountPrice\":%d,"
                        + "\"orderStatus\":\"%s\","
                        + "\"paidAt\":\"%s\","
                        + "\"productCount\":%d"
                        + "}",
                order.getId(),
                order.getUserId(),
                order.getTotalPrice(),
                order.getDiscountPrice(),
                order.getOrderStatus().name(),
                order.getPaidAt() != null ? order.getPaidAt().format(FORMATTER) : "null",
                order.getOrderProducts().size()
        );
    }
}