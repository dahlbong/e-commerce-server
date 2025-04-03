package kr.hhplus.be.server.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Coupon {
    private String id;
    private int discount; // 할인율 또는 할인 금액
    private String expiryDate;
    private String status; // AVAILABLE, USED, EXPIRED 등

    public Coupon(String id, int discount, String expiryDate, String status) {
        this.id = id;
        this.discount = discount;
        this.expiryDate = expiryDate;
        this.status = status;
    }

}