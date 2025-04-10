package kr.hhplus.be.server.api.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.application.product.PopularProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "인기 상품", description = "최근 인기 상품 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/popular-products")
public class PopularProductController {

    private final PopularProductService popularProductService;

    @Operation(summary = "인기 상품 조회", description = "최근 3일간 가장 많이 팔린 상품 상위 5개를 조회합니다.")
    @GetMapping
    public List<PopularProductResponse> getTopProducts() {
        return popularProductService.getTop5().stream()
                .map(PopularProductResponse::from)
                .toList();
    }
}