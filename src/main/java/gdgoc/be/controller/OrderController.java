package gdgoc.be.controller;

import gdgoc.be.common.ApiResponse;
import gdgoc.be.dto.OrderRequest;
import gdgoc.be.dto.OrderResponse;
import gdgoc.be.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ApiResponse<OrderResponse> createOrder(
            @RequestHeader("X-USER-ID") Long userId,
            @Valid @RequestBody OrderRequest orderRequest) {
        OrderResponse orderResponse = orderService.createOrder(userId, orderRequest);
        return ApiResponse.success(orderResponse);
    }

    @GetMapping
    public ApiResponse<List<OrderResponse>> getOrders(@RequestHeader("X-USER-ID") Long userId) {
        List<OrderResponse> orderResponses = orderService.getOrdersByUser(userId);
        return ApiResponse.success(orderResponses);
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getOrderDetails(
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable("id") Long orderId) {
        OrderResponse orderResponse = orderService.getOrderDetails(userId, orderId);
        return ApiResponse.success(orderResponse);
    }
}