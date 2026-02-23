package gdgoc.be.dto;

import gdgoc.be.domain.CartItem;
import lombok.Builder;

@Builder
public record CartResponse(
        Long cartId,
        Long productId,
        String productName,
        int quantity,
        int price,
        boolean isAvailable
) {
    public static CartResponse from(CartItem item) {
        return CartResponse.builder()
                .cartId(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .price(item.getProduct().getPrice())
                .isAvailable(item.getProduct().isAvailable())
                .build();
    }
}
