package gdgoc.be.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CartRequest(
        @NotNull(message = "상품 ID는 필수입니다.")
        long productId,

        @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
        int quantity,

        @NotBlank(message = "사이즈 선택은 필수입니다.")
        String selectedSize
) {
}
