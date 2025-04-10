package kr.hhplus.be.server.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Product API", description = "상품 정보 및 인기 상품 조회 API")
public class ProductController {

    private static List<Product> products = new ArrayList<>(Arrays.asList(
            new Product(1, "Product A", 100.0, 10),
            new Product(2, "Product B", 200.0, 5)
    ));

    private static List<PopularProduct> popularProducts = new ArrayList<>(Arrays.asList(
            new PopularProduct(1, "Product A", 50, 5000.0),
            new PopularProduct(2, "Product B", 30, 6000.0)
    ));

    @Operation(summary = "모든 상품 조회", description = "상품 ID, 이름, 가격, 재고 정보를 포함한 전체 상품 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "상품 목록 조회 성공")
    @GetMapping
    public List<Product> getAllProducts() {
        return products;
    }

    @Operation(summary = "상품 상세 조회", description = "특정 상품 ID에 해당하는 상품 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 상품 ID",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"status\":404,\"message\":\"Product not found\"}")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable int id) {
        return products.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "상품 등록", description = "신규 상품을 생성합니다. (ID는 자동 생성)")
    @ApiResponse(responseCode = "201", description = "상품 생성 성공")
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductRequest request) {
        int newId = products.size() + 1;
        Product newProduct = new Product(newId, request.getName(), request.getPrice(), request.getStock());
        products.add(newProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }

    @Operation(summary = "인기 상품 조회", description = "최근 판매량 기준으로 인기 상품 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "인기 상품 조회 성공")
    @GetMapping("/popular")
    public ResponseEntity<List<PopularProduct>> getPopularProducts() {
        return ResponseEntity.ok(popularProducts);
    }

}