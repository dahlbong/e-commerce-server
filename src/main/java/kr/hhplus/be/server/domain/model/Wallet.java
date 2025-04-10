package kr.hhplus.be.server.domain.model;

import lombok.Getter;

@Getter
public class Wallet {
    private String userId;
    private double balance;

    public Wallet(String userId, double balance) {
        this.userId = userId;
        this.balance = balance;
    }
}