package kr.hhplus.be.server.domain.product.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum SellingStatus {

    HOLD("판매 보류"),
    SELLING("판매 중"),
    STOP_SELLING("판매 중지"),
    ;
    private final String description;

    private static final List<SellingStatus> CANNOT_SELLING_STATUSES = List.of(HOLD, STOP_SELLING);

    public boolean cannotSelling() {
        return CANNOT_SELLING_STATUSES.contains(this);
    }

    public static List<SellingStatus> forSelling() {
        return List.of(SELLING);
    }
}