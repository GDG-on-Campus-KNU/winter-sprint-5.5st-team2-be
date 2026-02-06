package gdgoc.be.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartRequest {

    @NotNull(message = "메뉴 ID는 필수입니다.")
    private Long menuId;
    @Min(value = 1, message = "수량은 최소 1개 이상이어야 합니다.")
    @Positive(message = "양수만 입력 가능합니다.")
    private int quantity;
}
