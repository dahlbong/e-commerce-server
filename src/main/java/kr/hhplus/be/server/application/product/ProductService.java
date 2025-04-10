package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.BusinessException;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.enums.ProductErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllWithStock() {
        return productRepository.findAllWithStock();
    }

    public Product getById(Long id) {
        Product product = productRepository.findById(id);
        if (product == null) {
            throw new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND);
        }
        return product;
    }
}
