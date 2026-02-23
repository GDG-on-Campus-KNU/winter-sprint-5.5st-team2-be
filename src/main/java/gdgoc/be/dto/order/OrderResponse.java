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
        LocalDateTime orderDate,
        String orderStatus,
        String address,
        List<OrderItemResponse> orderItems,
        BigDecimal totalPrice,
        BigDecimal totalDiscount,
        BigDecimal shippingFee,
        BigDecimal finalPrice,
        Long couponId
) {

    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .orderDate(order.getOrderDate()) // orderDate 필드 사용
                .orderStatus(order.getStatus().name())
                .address(order.getAddress()) // 추가
                .orderItems(order.getOrderItems().stream()
                        .map(OrderItemResponse::from)
                        .collect(Collectors.toList()))
                .totalPrice(order.getTotalAmount()) // totalAmount 매핑
                .totalDiscount(order.getDiscountAmount()) // 매핑
                .shippingFee(order.getDeliveryFee())   // 매핑
                .finalPrice(order.getFinalAmount()) // 매핑
                .couponId(order.getCouponId()) // couponId 매핑
                .build();
    }
}