package kr.hhplus.be.server.infra.scheduler;

import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.product.PopularProduct;
import kr.hhplus.be.server.domain.product.PopularProductRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PopularProductScheduler {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PopularProductRepository popularProductRepository;

    @Scheduled(cron = "0 0 0 * * *") // 매일 0시에 실행
    public void collectPopularProducts() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeDaysAgo = now.minusDays(3);

        List<Object[]> result = orderRepository.findPopularProductIdsSince(LocalDateTime.now().minusDays(3), 5);
        List<PopularProduct> popularProducts = result.stream()
                .map(row -> {
                    Long productId = (Long) row[0];
                    Long totalQuantity = (Long) row[1];
                    Product product = productRepository.findById(productId);
                    return PopularProduct.of(productId, product.getName(), product.getPrice(), totalQuantity.intValue());
                })
                .collect(Collectors.toList());

        popularProductRepository.saveAll(popularProducts);
    }
}
