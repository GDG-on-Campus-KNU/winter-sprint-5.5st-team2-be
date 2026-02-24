package gdgoc.be.dto.order;

import gdgoc.be.domain.Order;
import lombok.Builder;
import java.util.List;

@Builder
public record OrderListResponse(
        String orderId,
        String representativeItem,
        Integer totalPrice,
        String orderStatus,
        String createdAt,
        List<OrderItemResponse> items
) {
    public static OrderListResponse from(Order order) {
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(OrderItemResponse::from)
                .toList();

        // 대표 상품명 생성 로직
        String repName = itemResponses.isEmpty() ? "" : itemResponses.get(0).productName();
        if (itemResponses.size() > 1) {
            repName += " 외 " + (itemResponses.size() - 1) + "건";
        }

        return OrderListResponse.builder()
                .orderId(String.valueOf(order.getId()))
                .representativeItem(repName)
                .totalPrice(order.getFinalAmount().intValue())
                .orderStatus(order.getStatus().name())
                .createdAt(order.getOrderDate().toString())
                .items(itemResponses)
                .build();
    }
}