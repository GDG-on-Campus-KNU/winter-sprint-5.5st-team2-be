package gdgoc.be.dto.order;

import gdgoc.be.domain.OrderItem;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderItemResponse(
        Long productId,
        String productName,
        String imageUrl,
        String selectedSize,
        Integer quantity,
        Integer orderPrice
) {
    public static OrderItemResponse from(OrderItem orderItem) {
        return new OrderItemResponse(
                orderItem.getProduct().getId(),
                orderItem.getProduct().getName(),
                orderItem.getProduct().getImageUrl(),
                orderItem.getSelectedSize(),
                orderItem.getQuantity(),
                orderItem.getOrderPrice().intValue()
        );
    }
}
