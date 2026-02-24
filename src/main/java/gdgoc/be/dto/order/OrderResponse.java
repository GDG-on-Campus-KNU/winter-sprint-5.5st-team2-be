package gdgoc.be.dto.order;

import gdgoc.be.domain.Order;
import lombok.Builder;

@Builder
public record OrderResponse(
        String orderId,
        String orderStatus,
        String paymentStatus,
        Integer totalPrice,
        String createdAt
) {

    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
                .orderId(String.valueOf(order.getId()))
                .orderStatus(order.getStatus().name())
                .paymentStatus(order.getPaymentStatus().name())
                .totalPrice(order.getFinalAmount().intValue())
                .createdAt(order.getOrderDate().toString())
                .build();
    }
}