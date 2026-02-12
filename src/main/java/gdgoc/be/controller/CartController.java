package gdgoc.be.controller;


import gdgoc.be.common.ApiResponse;
import gdgoc.be.dto.CartDeleteRequest;
import gdgoc.be.dto.CartRequest;
import gdgoc.be.dto.CartResponse;
import gdgoc.be.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ApiResponse<List<CartResponse>> getCart(@RequestHeader("X-USER-ID")Long userId) {

        List<CartResponse> responses = cartService.getCartItems(userId);
        return ApiResponse.success(responses);
    }

    @PostMapping
    public ApiResponse<String> addToCart(
            @RequestHeader("X-USER-ID") Long userId,
            @Valid @RequestBody CartRequest request) {
        cartService.addMenuToCart(userId, request.getMenuId(), request.getQuantity());
        return ApiResponse.success("장바구니에 상품을 담았습니다.");
    }

    @PatchMapping("/{itemId}")
    public ApiResponse<String> updateQuantity(
            @PathVariable Long itemId,
            @RequestBody CartRequest request) {
        cartService.updateCartItemQuantity(itemId, request.getQuantity());
        return ApiResponse.success("수량이 변경되었습니다.");
    }

    @DeleteMapping
    public ApiResponse<String> deleteCartItems(@RequestBody CartDeleteRequest request) {
        cartService.deleteSelectedItems(request.getItemIds());
        return ApiResponse.success("선택한 상품들을 삭제했습니다.");
    }
}
