package gdgoc.be.dto.product;

import gdgoc.be.domain.Product;
import lombok.*;

import java.util.List;

@Builder
public record ProductDetailResponse(
        Long id,
        String brand,
        String name,
        String imageUrl,
        int originalPrice,
        int discountRate,
        int price,
        Integer stock,
        boolean isAvailable,
        String description,
        List<String> sizesOptions,
        List<String> detailImages,
        List<String> galleryImages,
        Double rating,
        String category,
        String createdAt
) {

    public static ProductDetailResponse from(Product product) {
        return ProductDetailResponse.builder()
                .id(product.getId())
                .brand(product.getBrand())
                .name(product.getName())
                .imageUrl(product.getImageUrl())
                .originalPrice(product.getOriginalPrice())
                .discountRate(product.getDiscountRate())
                .price(product.getPrice())
                .stock(product.getStock())
                .isAvailable(product.isAvailable())
                .description(product.getDescription())
                .sizesOptions(product.getSizesOptions())
                .detailImages(product.getDetailImages())
                .galleryImages(product.getGalleryImages())
                .rating(product.getRating())
                .category(product.getCategory().name())
                .createdAt(product.getCreatedAt() != null ? product.getCreatedAt().toString() : null)
                .build();
    }
}
