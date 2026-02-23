package gdgoc.be.dto;

import jakarta.validation.constraints.NotNull;

public record CartDeleteRequest(
    @NotNull(message = "삭제할 장바구니 항목 ID는 필수입니다.")
    Long cartId
) {
}
