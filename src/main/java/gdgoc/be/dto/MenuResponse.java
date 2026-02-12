package gdgoc.be.dto;


import gdgoc.be.domain.Menu;
import lombok.Builder;
import lombok.Getter;


import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuResponse {
    private Long id;
    private String name;
    private int price;
    private String category;

    @Builder
    private MenuResponse(Long id, String name, int price, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public static MenuResponse from(Menu menu) {
        return MenuResponse.builder()
                .id(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .category(menu.getCategory().name())
                .build();
    }
}