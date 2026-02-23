package gdgoc.be.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderRequest(
    @Valid
    @NotNull(message = "주문할 상품 목록은 필수입니다.")
    List<OrderItemRequest> items,

    @Nullable
    Long couponId,

    @Nullable
    String address
) {
}
