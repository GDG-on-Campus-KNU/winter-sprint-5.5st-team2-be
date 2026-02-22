package gdgoc.be.controller;


import gdgoc.be.common.ApiResponse;
import gdgoc.be.dto.CartDeleteRequest;
import gdgoc.be.dto.CartRequest;
import gdgoc.be.dto.CartResponse;
import gdgoc.be.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // 1. 장바구니 목록 조회 (GET/api/cart)
    @GetMapping
    public ApiResponse<List<CartResponse>> getCart() {

        List<CartResponse> responses = cartService.getCartItems();
        return ApiResponse.success(responses);
    }

    // 2. 장바구니 담기 (POST /api/cart)
    @PostMapping
    public ApiResponse<String> addToCart(
            @Valid @RequestBody CartRequest request) {
        cartService.addMenuToCart(request.menuId(), request.quantity());
        return ApiResponse.success("장바구니에 상품을 담았습니다.");
    }

    @PatchMapping("/{itemId}")
    public ApiResponse<String> updateQuantity(
            @PathVariable Long itemId,
            @RequestBody CartRequest request) {
        //[요구사항] 수량이 0 이하이면 서비스에서 자동 삭제
        cartService.updateCartItemQuantity(itemId, request.quantity());
        return ApiResponse.success("수량이 변경되었습니다.");
    }

    @DeleteMapping
    public ApiResponse<String> deleteCartItems(@RequestBody CartDeleteRequest request) {
        cartService.deleteSelectedItems(request.itemIds());
        return ApiResponse.success("선택한 상품들을 삭제했습니다.");
    }
}
