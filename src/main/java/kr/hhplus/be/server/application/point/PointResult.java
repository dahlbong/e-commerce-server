package kr.hhplus.be.server.application.point;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointResult {

    @Getter
    public static class Point {

        private final long amount;

        private Point(long amount) {
            this.amount = amount;
        }

        public static Point of(long amount) {
            return new Point(amount);
        }
    }
}