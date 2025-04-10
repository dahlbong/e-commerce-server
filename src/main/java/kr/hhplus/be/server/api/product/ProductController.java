package kr.hhplus.be.server.api.product;

import kr.hhplus.be.server.application.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/products")
    public List<ProductResponse> getProducts() {
        return productService.getAllWithStock().stream()
                .map(ProductResponse::from)
                .toList();
    }
}
