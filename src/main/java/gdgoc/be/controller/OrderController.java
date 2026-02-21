package gdgoc.be.controller;

import gdgoc.be.common.ApiResponse;
import gdgoc.be.dto.OrderRequest;
import gdgoc.be.dto.OrderResponse;
import gdgoc.be.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "주문 생성", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ApiResponse<OrderResponse> createOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody OrderRequest orderRequest) {
        OrderResponse orderResponse = orderService.createOrder(userDetails.getUsername(), orderRequest);
        return ApiResponse.success(orderResponse);
    }

    @Operation(summary = "주문 목록 조회", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public ApiResponse<List<OrderResponse>> getOrders(@AuthenticationPrincipal UserDetails userDetails) {
        List<OrderResponse> orderResponses = orderService.getOrdersByUser(userDetails.getUsername());
        return ApiResponse.success(orderResponses);
    }

    // 3. 주문 상세 조회 (GET /api/orders/{id})
    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getOrderDetails(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("id") Long orderId) {
        OrderResponse orderResponse = orderService.getOrderDetails(userDetails.getUsername(), orderId);
        return ApiResponse.success(orderResponse);
    }
}