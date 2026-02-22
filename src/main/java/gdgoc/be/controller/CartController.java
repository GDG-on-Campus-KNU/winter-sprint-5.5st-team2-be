package gdgoc.be.controller;

import gdgoc.be.common.ApiResponse;
import gdgoc.be.dto.CartDeleteRequest;
import gdgoc.be.dto.CartRequest;
import gdgoc.be.dto.CartResponse;
import gdgoc.be.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Cart", description = "장바구니 관련 API")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(summary = "장바구니 담기", description = "상품을 장바구니에 추가합니다.", 
               security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ApiResponse<Void> addCart(@Valid @RequestBody CartRequest request) {
        cartService.addProductToCart(request.productId(), request.quantity());
        return ApiResponse.success(null);
    }

    @Operation(summary = "내 장바구니 조회", description = "현재 사용자의 장바구니 목록을 조회합니다.", 
               security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public ApiResponse<List<CartResponse>> getMyCart() {
        return ApiResponse.success(cartService.getMyCart());
    }

    @Operation(summary = "장바구니 항목 삭제", description = "장바구니에서 특정 상품을 제거합니다.", 
               security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping
    public ApiResponse<Void> deleteCart(@Valid @RequestBody CartDeleteRequest request) {
        cartService.deleteCartItem(request);
        return ApiResponse.success(null);
    }
}
