package gdgoc.be.dto;

import gdgoc.be.domain.CartItem;
import gdgoc.be.domain.Menu;

import java.util.List;

public record CartItemResponse(
        Long cartItemId,       // 장바구니 아이템 고유 ID
        Long productId,        // 상품 고유 ID (Menu ID)
        String brand,          // 브랜드명
        String name,           // 상품명
        String imageUrl,       // 대표 이미지 URL
        int originalPrice,     // 정가
        int discountRate,      // 할인율 (%)
        int price,             // 판매가
        String selectedSize,   // 선택한 사이즈
        List<String> sizeOptions, // 선택 가능한 옵션 목록
        int quantity           // 수량
) {
    public static CartItemResponse from(CartItem item) {
        Menu menu = item.getMenu();

        // 할인율 계산 (정가가 있을 경우)
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
                menu.getSizeOptions(), // Menu 엔티티에 List<String> 필드 추가 필요
                item.getQuantity()
        );
    }
}