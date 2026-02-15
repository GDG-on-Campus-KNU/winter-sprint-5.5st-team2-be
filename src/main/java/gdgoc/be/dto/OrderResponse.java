package gdgoc.be.dto;

import gdgoc.be.domain.Order;
import gdgoc.be.domain.OrderItem;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class OrderResponse {
    private Long orderId;
    private LocalDateTime orderDate;
    private String orderStatus;
    private String address; // 추가
    private List<OrderItemResponse> orderItems;
    private BigDecimal totalPrice;
    private BigDecimal totalDiscount; // 임시 값
    private BigDecimal shippingFee;   // 임시 값
    private BigDecimal finalPrice;
    private Long couponId;

    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .orderDate(order.getCreatedAt()) // orderDate는 createdAt으로 매핑
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

    @Getter
    @Setter
    @Builder
    public static class OrderItemResponse {
        private Long orderItemId;
        private Long menuId;
        private String menuName;
        private BigDecimal pricePerItem; // 변경
        private int quantity;

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
}