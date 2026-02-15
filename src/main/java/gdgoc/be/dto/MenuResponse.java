package gdgoc.be.dto;


import gdgoc.be.domain.Menu;
import lombok.Builder;

@Builder
public record MenuResponse(
        Long id,
        String name,
        int price,
        String category
) {

    public static MenuResponse from(Menu menu) {
        return MenuResponse.builder()
                .id(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .category(menu.getCategory().name())
                .build();
    }
}