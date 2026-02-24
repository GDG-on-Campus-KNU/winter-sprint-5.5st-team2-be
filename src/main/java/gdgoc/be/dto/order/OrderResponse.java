package gdgoc.be.dto.order;

import gdgoc.be.domain.Order;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record OrderResponse(
        Long orderId,
        String representativeItem,
        LocalDateTime createdAt,
        String orderStatus,
        String paymentStatus,
        String shippingAddress,
        List<OrderItemResponse> items,
        BigDecimal totalPrice,
        BigDecimal totalDiscount,
        BigDecimal shippingFee,
        Long couponId
) {

    public static OrderResponse from(Order order) {
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(OrderItemResponse::from)
                .toList();

        String repName = itemResponses.get(0).productName();
        if (itemResponses.size() > 1) {
            repName += " 외 " + (itemResponses.size() - 1) + "건";
        }

        return OrderResponse.builder()
                .orderId(order.getId())
                .representativeItem(repName)
                .createdAt(order.getOrderDate())
                .orderStatus(order.getStatus().name())
                .paymentStatus(order.getPaymentStatus().name())
                .shippingAddress(order.getAddress())
                .items(itemResponses)
                .totalPrice(order.getFinalAmount())
                .totalDiscount(order.getDiscountAmount())
                .shippingFee(order.getDeliveryFee())
                .couponId(order.getCouponId())
                .build();
    }
}