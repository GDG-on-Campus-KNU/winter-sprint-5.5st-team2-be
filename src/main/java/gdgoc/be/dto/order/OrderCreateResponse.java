package gdgoc.be.dto.order;

import gdgoc.be.domain.Order;
import lombok.Builder;

@Builder
public record OrderCreateResponse(
        String orderId,
        String orderStatus,
        String paymentStatus,
        Integer totalPrice,
        String createdAt
) {
    public static OrderCreateResponse from(Order order) {
        return OrderCreateResponse.builder()
                .orderId(String.valueOf(order.getId()))
                .orderStatus(order.getStatus().name())
                .paymentStatus(order.getPaymentStatus().name())
                .totalPrice(order.getFinalAmount().intValue())
                .createdAt(order.getOrderDate().toString())
                .build();
    }
}