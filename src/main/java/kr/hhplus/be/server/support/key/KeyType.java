package kr.hhplus.be.server.support.key;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KeyType {

    RANK("랭킹"),
    USER_COUPON("쿠폰");

    private final String description;

    public String getKey() {
        return this.name().toLowerCase();
    }
}