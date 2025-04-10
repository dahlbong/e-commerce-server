package kr.hhplus.be.server.domain.model;

import lombok.Getter;

@Getter
public class Order {
    private String orderId;
    private String userId;
    private double totalAmount;
    private String status;
    private String orderDate;

    public Order(String orderId, String userId, double totalAmount, String status, String orderDate) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderDate = orderDate;
    }
}