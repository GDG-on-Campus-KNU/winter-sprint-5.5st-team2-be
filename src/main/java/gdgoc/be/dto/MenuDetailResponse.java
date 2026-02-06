package gdgoc.be.dto;

import lombok.*;

@Getter @Builder
@NoArgsConstructor @AllArgsConstructor
public class MenuDetailResponse {
    private Long id;
    private String name;
    private int price;
    private String description;
    private Integer stock;
    private String status;
    private String category;
    private boolean isAvailable; // 재고가 0 보다 클 시 true 를 저장
}