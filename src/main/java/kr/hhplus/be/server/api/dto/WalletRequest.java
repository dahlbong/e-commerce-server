package kr.hhplus.be.server.api.dto;

import lombok.Getter;

@Getter
public class WalletRequest {
    private String userId;
    private double amount;

    public WalletRequest(String userId, double amount) {
        this.userId = userId;
        this.amount = amount;
    }
}
