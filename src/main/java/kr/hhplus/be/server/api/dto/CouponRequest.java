package kr.hhplus.be.server.api.dto;

import lombok.Getter;

@Getter
public class CouponRequest {
    private String userId;

    public CouponRequest() {}

    public CouponRequest(String userId) {
        this.userId = userId;
    }

}