package kr.hhplus.be.server.api.product;

import kr.hhplus.be.server.domain.product.PopularProduct;

import java.math.BigDecimal;

public record PopularProductResponse(
        Long productId,
        String name,
        BigDecimal price,
        int totalOrderCount
) {
    public static PopularProductResponse from(PopularProduct p) {
        return new PopularProductResponse(p.getProductId(), p.getName(), p.getPrice(), p.getTotalOrderCount());
    }
}