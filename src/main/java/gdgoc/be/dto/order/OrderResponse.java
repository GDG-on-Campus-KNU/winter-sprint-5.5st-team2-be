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
        List<OrderItemResponse> itemNum = order.getOrderItems().stream()
                .map(OrderItemResponse::from)
                .toList();

        String repName = itemNum.get(0).productName();
        if (itemNum.size() > 1) {
            repName += " 외 " + (itemNum.size() - 1) + "건";
        }

        return new OrderResponse(
                order.getId(),
                repName,
                order.getOrderDate(),
                order.getStatus().name(),
                order.getFinalAmount(),
                itemNum
        );
    }
}