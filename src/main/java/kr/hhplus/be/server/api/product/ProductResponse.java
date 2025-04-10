package kr.hhplus.be.server.api.product;

import kr.hhplus.be.server.domain.product.Product;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        BigDecimal price,
        boolean selling,
        int remainQuantity
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.isSelling(),
                product.getRemainQuantity()
        );
    }
}
