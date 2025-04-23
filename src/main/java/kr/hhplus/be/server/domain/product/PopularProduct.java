package kr.hhplus.be.server.domain.product;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "popular_product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PopularProduct {

    @Id
    private Long productId;

    private String name;

    private BigDecimal price;

    private int totalOrderCount;

    private LocalDateTime createdAt;

    public static PopularProduct of(Long productId, String name, BigDecimal price, int totalOrderCount) {
        return new PopularProduct(productId, name, price, totalOrderCount, LocalDateTime.now());
    }


}
