package gdgoc.be.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


public record OrderRequest(

        @Valid
        @NotNull(message = "주문할 상품 목록은 필수입니다.")
        List<OrderItemRequest> orderItems,

        @Nullable
        Long couponId
) {
}
