package kr.hhplus.be.server.api.dto;


public class OrderResponse {
    private String orderId;
    private String status;
    private String userId;
    private double totalAmount;

    public OrderResponse(String orderId, String status, String userId, double totalAmount) {
        this.orderId = orderId;
        this.status = status;
        this.userId = userId;
        this.totalAmount = totalAmount;
    }
}