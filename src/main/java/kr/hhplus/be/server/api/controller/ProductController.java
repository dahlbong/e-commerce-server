package kr.hhplus.be.server.api.controller;


import kr.hhplus.be.server.api.dto.ProductRequest;
import kr.hhplus.be.server.domain.model.PopularProduct;
import kr.hhplus.be.server.domain.model.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    // 더미 데이터: 초기 상품 목록
    private static List<Product> products = new ArrayList<>(Arrays.asList(
            new Product(1, "Product A", 100.0, 10),
            new Product(2, "Product B", 200.0, 5)
    ));
    // 더미 데이터: 인기 판매 상품 목록
    private static List<PopularProduct> popularProducts = new ArrayList<>(Arrays.asList(
            new PopularProduct(1, "Product A", 50, 5000.0),
            new PopularProduct(2, "Product B", 30, 6000.0)
    ));

    // 모든 상품 조회
    @GetMapping
    public List<Product> getAllProducts() {
        return products;
    }

    // 상품 ID로 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable int id) {
        return products.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // 새로운 상품 생성
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductRequest request) {
        int newId = products.size() + 1;
        Product newProduct = new Product(newId, request.getName(), request.getPrice(), request.getStock());
        products.add(newProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }

    // 인기 판매 상품 조회 API 추가
    @GetMapping("/popular")
    public ResponseEntity<List<PopularProduct>> getPopularProducts() {
        return ResponseEntity.ok(popularProducts);
    }

}