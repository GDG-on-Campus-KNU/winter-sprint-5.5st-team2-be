package gdgoc.be.dto;


import gdgoc.be.domain.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CartResponse {

    private Long itemId;
    private String menuName;
    private int price;
    private int quantity;
    private boolean isAvailable;

    @Builder
    private CartResponse(Long itemId, String menuName, int price, int quantity, boolean isAvailable) {
        this.itemId = itemId;
        this.menuName = menuName;
        this.price = price;
        this.quantity = quantity;
        this.isAvailable = isAvailable;
    }

    public static CartResponse from(CartItem item) {
        return CartResponse.builder()
                .itemId(item.getId())
                .menuName(item.getMenu().getName())
                .price(item.getMenu().getPrice())
                .quantity(item.getQuantity())
                .isAvailable(item.getMenu().getStock() > 0) // 재고가 있으면 주문 가능
                .build();
    }
}
