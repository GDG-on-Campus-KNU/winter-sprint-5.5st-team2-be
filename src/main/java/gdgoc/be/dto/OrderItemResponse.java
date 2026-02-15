package gdgoc.be.dto;

import gdgoc.be.domain.OrderItem;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderItemResponse(
        Long orderItemId,
        Long menuId,
        String menuName,
        BigDecimal pricePerItem, // 변경
        int quantity
) {

    public static OrderItemResponse from(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .orderItemId(orderItem.getId())
                .menuId(orderItem.getMenu().getId())
                .menuName(orderItem.getMenu().getName())
                .pricePerItem(orderItem.getOrderPrice())
                .quantity(orderItem.getQuantity())
                .build();
    }
}