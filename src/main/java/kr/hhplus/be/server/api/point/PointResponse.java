package kr.hhplus.be.server.api.point;

import kr.hhplus.be.server.domain.point.Point;

import java.math.BigDecimal;

public record PointResponse(Long userId, BigDecimal balance) {
    public static PointResponse from(Point point) {
        return new PointResponse(point.getUserId(), point.getBalance());
    }
}
