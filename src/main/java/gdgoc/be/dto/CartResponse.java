package gdgoc.be.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {

    private Long itemId;
    private String menuName;
    private int price;
    private int quantity;
    private boolean isAvailable; // [요구사항] 품절 여부 판단 필드
}
