package gdgoc.be.dto;


import java.util.List;

public record CartResponse(
        List<CartItemResponse> items,
        int subtotal,
        int shippingFee,
        int total
) {
    public static CartResponse of(List<CartItemResponse> items, int subtotal, int shippingFee) {
        return new CartResponse(items, subtotal, shippingFee, subtotal + shippingFee);
    }
}