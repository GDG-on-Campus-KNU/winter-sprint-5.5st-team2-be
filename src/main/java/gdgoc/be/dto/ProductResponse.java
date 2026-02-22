package gdgoc.be.dto;

import gdgoc.be.domain.Product;
import lombok.Builder;

@Builder
public record ProductResponse(
        Long id,
        String brand,
        String name,
        String imageUrl,
        int originalPrice,
        int discountRate,
        int price,
        Integer stock,
        boolean isAvailable,
        String category
) {

    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .brand(product.getBrand())
                .name(product.getName())
                .imageUrl(product.getImageUrl())
                .originalPrice(product.getOriginalPrice())
                .discountRate(product.getDiscountRate())
                .price(product.getPrice())
                .stock(product.getStock())
                .isAvailable(product.isAvailable())
                .category(product.getCategory().name())
                .build();
    }
}
