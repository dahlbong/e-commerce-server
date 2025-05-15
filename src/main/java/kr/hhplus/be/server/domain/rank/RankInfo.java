package kr.hhplus.be.server.domain.rank;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RankInfo {

    public record PopularProduct(Long productId, Long totalScore) {

    public static PopularProduct of(Long productId, Long totalScore) {
            return new PopularProduct(productId, totalScore);
        }
    }

    @Getter
    public static class PopularProducts {

        private final List<PopularProduct> products;

        private PopularProducts(List<PopularProduct> products) {
            this.products = products;
        }

        public static PopularProducts of(List<PopularProduct> products) {
            return new PopularProducts(products);
        }

        public List<Long> getProductIds() {
            return products.stream()
                    .map(PopularProduct::productId)
                    .toList();
        }
    }
}