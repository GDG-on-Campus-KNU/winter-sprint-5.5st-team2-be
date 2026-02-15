package gdgoc.be.dto;


import gdgoc.be.domain.CartItem;
import lombok.Builder;

@Builder
public record CartResponse(
        Long itemId,
        String menuName,
        int price,
        int quantity,
        boolean isAvailable
) {

    public static CartResponse from(CartItem item) {
        return CartResponse.builder()
                .itemId(item.getId())
                .menuName(item.getMenu().getName())
                .price(item.getMenu().getPrice())
                .quantity(item.getQuantity())
                .isAvailable(item.getMenu().getStock() >0)
                .build();
    }
}