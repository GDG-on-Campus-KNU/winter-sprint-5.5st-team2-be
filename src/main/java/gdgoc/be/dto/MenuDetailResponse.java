package gdgoc.be.dto;

import gdgoc.be.domain.Menu;
import lombok.*;

@Getter
@NoArgsConstructor
public class MenuDetailResponse {
    private Long id;
    private String name;
    private int price;
    private String description;
    private Integer stock;
    private String status;
    private String category;
    private boolean isAvailable;

    @Builder
    private MenuDetailResponse(Long id, String name, int price, String description, Integer stock, String status, String category, boolean isAvailable) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.stock = stock;
        this.status = status;
        this.category = category;
        this.isAvailable = isAvailable;
    }

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