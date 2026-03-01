package gdgoc.be.controller;

import gdgoc.be.common.api.ApiResponse;
import gdgoc.be.dto.product.ProductDetailResponse;
import gdgoc.be.dto.product.ProductResponse;
import gdgoc.be.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Product", description = "상품 관련 API")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "상품 목록 조회", description = "카테고리, 검색어, 정렬 조건에 따라 상품 목록을 조회합니다. limit으로 개수를 제한할 수 있습니다.")
    @GetMapping
    public ApiResponse<List<ProductResponse>> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer limit) {

        List<ProductResponse> products = productService.findAllProducts(category, search, sort, limit);
        return ApiResponse.success(products);
    }

    @Operation(summary = "상품 상세 조회", description = "ID를 통해 특정 상품의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ApiResponse<ProductDetailResponse> getProductDetail(@PathVariable Long id) {
        return ApiResponse.success(productService.findProductDetail(id));
    }

    @Operation(summary = "상품 검색", description = "검색어를 통해 상품을 검색합니다.")
    @GetMapping("/search")
    public ApiResponse<List<ProductResponse>> searchProducts(@RequestParam String q) {
        return ApiResponse.success(productService.findAllProducts(null, q, null));
    }
}
