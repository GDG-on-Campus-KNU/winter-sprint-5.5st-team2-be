package gdgoc.be.dto;

import gdgoc.be.domain.Menu;
import lombok.*;

@Builder
public record MenuDetailResponse(
        Long id,
        String name,
        int price,
        String description,
        Integer stock,
        String status,
        String category,
        boolean isAvailable
) {

    public static MenuDetailResponse from(Menu menu) {

        return MenuDetailResponse.builder()
                .id(menu.getId())
                .name(builder().name)
                .price(menu.getPrice())
                .description(menu.getDescription())
                .category(menu.getCategory().name())
                .isAvailable(menu.isAvailable())
                .status(menu.getStock() >0 ? "판매 중" : "품절")
                .build();
    }
}