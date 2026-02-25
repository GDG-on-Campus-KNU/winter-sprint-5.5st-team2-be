package gdgoc.be.service;

import gdgoc.be.Repository.ProductRepository;
import gdgoc.be.domain.Category;
import gdgoc.be.domain.Product;
import gdgoc.be.dto.product.ProductDetailResponse;
import gdgoc.be.dto.product.ProductResponse;
import gdgoc.be.exception.BusinessErrorCode;
import gdgoc.be.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductResponse> findAllProducts(String categoryStr, String search, String sort) {
        Category category = null;
        if (categoryStr != null && !categoryStr.isEmpty()) {
            try {
                category = Category.valueOf(categoryStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Ignore or handle invalid category
            }
        }

        List<Product> products = productRepository.findAllWithFilters(category, search);

        // TODO: Implement sorting logic based on the 'sort' parameter if needed
        
        return products.stream()
                .map(ProductResponse::from)
                .toList();
    }

    public ProductDetailResponse findProductDetail(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.PRODUCT_NOT_FOUND));
        return ProductDetailResponse.from(product);
    }
}
