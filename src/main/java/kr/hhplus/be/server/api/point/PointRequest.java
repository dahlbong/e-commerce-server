package kr.hhplus.be.server.api.point;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class PointRequest {
    private Long userId;
    private BigDecimal amount;
}