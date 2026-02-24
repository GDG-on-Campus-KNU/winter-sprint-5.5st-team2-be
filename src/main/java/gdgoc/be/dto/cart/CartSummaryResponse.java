package gdgoc.be.dto.cart;

import lombok.Builder;

import java.util.List;

@Builder
public record CartSummaryResponse(
    List<CartResponse> items,
    int subtotal,
    int shippingFee,
    int total
    )
{}
