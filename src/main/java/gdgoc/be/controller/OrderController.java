package gdgoc.be.controller;

import gdgoc.be.common.ApiResponse;
import gdgoc.be.dto.OrderRequest;
import gdgoc.be.dto.OrderResponse;
import gdgoc.be.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다.")
    @PostMapping
    public ApiResponse<OrderResponse> createOrder(
            @Parameter(description = "사용자 ID") @RequestHeader("X-USER-ID") Long userId,
            @Valid @RequestBody OrderRequest orderRequest) {
        OrderResponse orderResponse = orderService.createOrder(userId, orderRequest);
        return ApiResponse.success(orderResponse);
    }

    @Operation(summary = "주문 목록 조회", description = "사용자의 모든 주문 목록을 조회합니다.")
    @GetMapping
    public ApiResponse<List<OrderResponse>> getOrders(@Parameter(description = "사용자 ID") @RequestHeader("X-USER-ID") Long userId) {
        List<OrderResponse> orderResponses = orderService.getOrdersByUser(userId);
        return ApiResponse.success(orderResponses);
    }

    @Operation(summary = "주문 상세 조회", description = "특정 주문의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getOrderDetails(
            @Parameter(description = "사용자 ID") @RequestHeader("X-USER-ID") Long userId,
            @Parameter(description = "주문 ID") @PathVariable("id") Long orderId) {
        OrderResponse orderResponse = orderService.getOrderDetails(userId, orderId);
        return ApiResponse.success(orderResponse);
    }
}