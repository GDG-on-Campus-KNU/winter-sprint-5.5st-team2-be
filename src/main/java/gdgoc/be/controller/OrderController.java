package gdgoc.be.controller;

import gdgoc.be.common.api.ApiResponse;
import gdgoc.be.dto.order.OrderConfirmRequest;
import gdgoc.be.dto.order.OrderRequest;
import gdgoc.be.dto.order.OrderResponse;
import gdgoc.be.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Order", description = "주문 관련 API")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @Operation(summary = "주문 목록 조회", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public ApiResponse<Page<OrderResponse>> getOrders(Pageable pageable) {
        // 명세서 공통 규칙: page, size, totalElements 등 처리를 위해 Page 반환
        return ApiResponse.success(orderService.getMyOrders(pageable));
    }

    @Operation(summary = "주문 생성", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ApiResponse<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        OrderResponse orderResponse = orderService.createOrder(orderRequest);
        return ApiResponse.success(orderResponse);
    }


    @Operation(summary = "주문 상세 조회", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getOrderDetails(@PathVariable("id") Long orderId) {
        return ApiResponse.success(orderService.getOrderDetails(orderId));
    }

    @Operation(summary = "주문 취소", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/{orderId}/cancel")
    public ApiResponse<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ApiResponse.success(null);
    }

    @Operation(summary = "결제 승인", description = "PG 연동 후 최종 결제를 확정합니다.")
    @PostMapping("/payments/confirm")
    public ApiResponse<Boolean> confirmPayment(@Valid @RequestBody OrderConfirmRequest request) {
        return ApiResponse.success(orderService.confirmPayment(
                request.paymentKey(),
                request.orderId(),
                request.amount()
        ));
    }
}
