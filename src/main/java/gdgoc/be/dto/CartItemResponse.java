package gdgoc.be.dto;

import gdgoc.be.domain.CartItem;
import gdgoc.be.domain.Menu;

import java.util.List;

public record CartItemResponse(
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
    public static CartItemResponse from(CartItem item) {

        Menu menu = item.getMenu();

        int discountRate = 0;
        if (menu.getOriginalPrice() > 0) {
            discountRate = (int) (((double) (menu.getOriginalPrice() - menu.getPrice()) / menu.getOriginalPrice()) * 100);
        }

        return new CartItemResponse(
                item.getId(),
                menu.getId(),
                menu.getBrand(),
                menu.getName(),
                menu.getImageUrl(),
                menu.getOriginalPrice(),
                discountRate,
                menu.getPrice(),
                item.getSelectedSize(),
                menu.getSizeOptions(),
                item.getQuantity()
        );
    }
}