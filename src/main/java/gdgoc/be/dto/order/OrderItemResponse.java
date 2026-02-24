package gdgoc.be.dto.order;

import gdgoc.be.domain.OrderItem;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderItemResponse(
        Long productId,
        String productName,
        int quantity,
        BigDecimal orderPrice
) {
    public static OrderItemResponse from(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .productId(orderItem.getProduct().getId())
                .productName(orderItem.getProduct().getName())
                .quantity(orderItem.getQuantity())
                .orderPrice(orderItem.getOrderPrice())
                .build();
    }
}
