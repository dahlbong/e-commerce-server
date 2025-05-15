package kr.hhplus.be.server.domain.point;

import java.math.BigDecimal;

public record PointCommand(Long userId, BigDecimal amount) {

    public record Charge(Long userId, BigDecimal amount) {}
    public record Use(Long userId, BigDecimal amount) {}
}
