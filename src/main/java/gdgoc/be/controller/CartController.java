package gdgoc.be.controller;

import gdgoc.be.common.ApiResponse;
import gdgoc.be.dto.CartDeleteRequest;
import gdgoc.be.dto.CartRequest;
import gdgoc.be.dto.CartResponse;
import gdgoc.be.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ApiResponse<CartResponse> getCart() {
        return ApiResponse.success(cartService.getCartItems());
    }

    @PostMapping
    public ApiResponse<String> addToCart(
            @Valid @RequestBody CartRequest request) {
        cartService.addMenuToCart(request.menuId(), request.quantity(), request.selectedSize());
        return ApiResponse.success("장바구니에 상품을 담았습니다.");
    }

    @PatchMapping("/{cartItemId}")
    public ApiResponse<String> updateQuantity(
            @PathVariable Long cartItemId,
            @RequestBody CartRequest request) {
        cartService.updateCartItem(cartItemId, request.quantity(), request.selectedSize());
        return ApiResponse.success("수량이 변경되었습니다.");
    }

    @DeleteMapping("/{cartItemId}")
    public ApiResponse<String> deleteItem(@PathVariable Long cartItemId) {
        cartService.deleteCartItem(cartItemId);
        return ApiResponse.success("상품이 삭제되었습니다.");
    }

    @DeleteMapping
    public ApiResponse<String> delectItems(@RequestBody CartDeleteRequest request) {

        if(request == null || request.itemIds() == null || request.itemIds().isEmpty()) {
            cartService.deleteAllItems();
            return ApiResponse.success("장바구니가 전체 삭제되었습니다.");
        }
        cartService.deleteSelectedItems(request.itemIds());
        return ApiResponse.success("선택한 아이템이 삭제되었습니다.");
    }
}
