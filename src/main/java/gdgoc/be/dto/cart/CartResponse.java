package gdgoc.be.dto.cart;

import gdgoc.be.domain.CartItem;
import lombok.Builder;

import java.util.List;

@Builder
public record CartResponse(
        Long cartItemId,
        Long productId,
        String brand,
        String name,
        String imageUrl,
        int originalPrice,
        int discountRate,
        int price,
        String selectedSize,
        List<String> sizeOptions,
        int quantity
) {
    public static CartResponse from(CartItem item) {
        return CartResponse.builder()
                .cartItemId(item.getId())
                .productId(item.getProduct().getId())
                .brand(item.getProduct().getBrand())
                .name(item.getProduct().getName())
                .imageUrl(item.getProduct().getImageUrl())
                .originalPrice(item.getProduct().getOriginalPrice())
                .discountRate(item.getProduct().getDiscountRate())
                .price(item.getProduct().getPrice())
                .selectedSize("FREE")
                .sizeOptions(item.getProduct().getSizesOptions())
                .quantity(item.getQuantity())
                .build();
    }
}
