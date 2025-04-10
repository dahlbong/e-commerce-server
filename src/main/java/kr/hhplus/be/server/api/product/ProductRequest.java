package kr.hhplus.be.server.api.product;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ProductRequest {
    private String name;
    private BigDecimal price;
    private int stock;
}
