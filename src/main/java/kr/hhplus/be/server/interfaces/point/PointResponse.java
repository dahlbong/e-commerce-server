package kr.hhplus.be.server.interfaces.point;

import kr.hhplus.be.server.application.point.PointResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointResponse {

    @Getter
    @NoArgsConstructor
    public static class Point {

        private Long amount;

        private Point(Long amount) {
            this.amount = amount;
        }

        public static Point of(PointResult.Point point) {
            return new Point(point.getAmount());
        }
    }
}