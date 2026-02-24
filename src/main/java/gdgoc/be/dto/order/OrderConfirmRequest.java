package gdgoc.be.dto.order;

import jakarta.validation.constraints.NotNull;

public record OrderConfirmRequest(
        @NotNull(message = "결제 키는 필수입니다.")
        String paymentKey,

        @NotNull(message = "주문 ID는 필수입니다.")
        Long orderId,

        @NotNull(message = "결제 금액은 필수입니다.")
        Integer amount
) {}