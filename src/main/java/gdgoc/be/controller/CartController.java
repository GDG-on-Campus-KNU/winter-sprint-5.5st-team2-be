package gdgoc.be.controller;


import gdgoc.be.common.ApiResponse;
import gdgoc.be.dto.CartDeleteRequest;
import gdgoc.be.dto.CartRequest;
import gdgoc.be.dto.CartResponse;
import gdgoc.be.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(summary = "장바구니 조회", description = "사용자의 장바구니에 담긴 상품 목록을 조회합니다.")
    @GetMapping
    public ApiResponse<List<CartResponse>> getCart(@Parameter(description = "사용자 ID") @RequestHeader("X-USER-ID")Long userId) {

        List<CartResponse> responses = cartService.getCartItems(userId);
        return ApiResponse.success(responses);
    }

    @Operation(summary = "장바구니에 상품 추가", description = "장바구니에 상품을 추가합니다.")
    @PostMapping
    public ApiResponse<String> addToCart(
            @Parameter(description = "사용자 ID") @RequestHeader("X-USER-ID") Long userId,
            @Valid @RequestBody CartRequest request) {
        cartService.addMenuToCart(userId, request.menuId(), request.quantity());
        return ApiResponse.success("장바구니에 상품을 담았습니다.");
    }

    @Operation(summary = "장바구니 상품 수량 변경", description = "장바구니에 담긴 특정 상품의 수량을 변경합니다.")
    @PatchMapping("/{itemId}")
    public ApiResponse<String> updateQuantity(
            @Parameter(description = "장바구니 상품 ID") @PathVariable Long itemId,
            @RequestBody CartRequest request) {
        cartService.updateCartItemQuantity(itemId, request.quantity());
        return ApiResponse.success("수량이 변경되었습니다.");
    }

    @Operation(summary = "장바구니 상품 삭제", description = "장바구니에서 선택한 상품들을 삭제합니다.")
    @DeleteMapping
    public ApiResponse<String> deleteCartItems(@RequestBody CartDeleteRequest request) {
        cartService.deleteSelectedItems(request.itemIds());
        return ApiResponse.success("선택한 상품들을 삭제했습니다.");
    }
}
