package kr.hhplus.be.server.domain.product;

public class PopularProduct {
    private int productId;
    private String name;
    private int salesQuantity;
    private double revenue;

    public PopularProduct(int productId, String name, int salesQuantity, double revenue) {
        this.productId = productId;
        this.name = name;
        this.salesQuantity = salesQuantity;
        this.revenue = revenue;
    }

}
