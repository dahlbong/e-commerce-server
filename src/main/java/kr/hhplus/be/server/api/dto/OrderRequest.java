package kr.hhplus.be.server.api.dto;

import kr.hhplus.be.server.domain.model.Product;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderRequest {
    private String userId;
    private List<Product> items;
    private String couponId; // 선택적

    public OrderRequest(String userId, List<Product> items, String couponId) {
        this.userId = userId;
        this.items = items;
        this.couponId = couponId;
    }
}