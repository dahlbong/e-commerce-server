package kr.hhplus.be.server.api.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.application.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "상품", description = "상품 관련 API")
@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "전체 상품 목록 조회", description = "모든 상품의 정보와 잔여 수량을 조회합니다.")
    @GetMapping("/products")
    public List<ProductResponse> getProducts() {
        return productService.getAllWithStock().stream()
                .map(ProductResponse::from)
                .toList();
    }
}
