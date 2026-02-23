package gdgoc.be.dto.cart;

public record CartUpdateRequest(
        Integer quantity,
        String selectedSize) {
}
