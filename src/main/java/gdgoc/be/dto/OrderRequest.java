package gdgoc.be.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {
    @Valid
    @NotNull(message = "주문할 상품 목록은 필수입니다.")
    private List<OrderItemRequest> orderItems;

    @Nullable
    private Long couponId;

    @Getter
    @Setter
    public static class OrderItemRequest {
        @NotNull(message = "메뉴 ID는 필수입니다.")
        private Long menuId;

        @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
        private int quantity;
    }
}