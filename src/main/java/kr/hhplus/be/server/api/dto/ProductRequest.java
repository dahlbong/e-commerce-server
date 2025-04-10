package kr.hhplus.be.server.api.dto;

import lombok.Getter;

@Getter
public class ProductRequest {
    private String name;
    private double price;
    private int stock;
}
