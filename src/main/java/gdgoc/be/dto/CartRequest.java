package gdgoc.be.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartRequest(

        @NotNull(message = "메뉴 ID는 필수입니다.")
        long menuId,

        @Min(value = 1, message = "수량은 최소 1개 이상이어야 합니다.")
        @Positive(message = "양수만 입력 가능합니다.")
        int quantity,
        String selectedSize
) {
}