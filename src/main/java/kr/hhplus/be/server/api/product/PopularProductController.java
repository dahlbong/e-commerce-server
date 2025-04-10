package kr.hhplus.be.server.api.product;

import kr.hhplus.be.server.application.product.PopularProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/popular-products")
public class PopularProductController {

    private final PopularProductService popularProductService;

    @GetMapping
    public List<PopularProductResponse> getTopProducts() {
        return popularProductService.getTop5().stream()
                .map(PopularProductResponse::from)
                .toList();
    }
}