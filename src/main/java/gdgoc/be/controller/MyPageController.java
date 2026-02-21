package gdgoc.be.controller;


import gdgoc.be.common.ApiResponse;
import gdgoc.be.common.util.SecurityUtil;
import gdgoc.be.dto.OrderResponse;
import gdgoc.be.dto.UserCouponResponse;
import gdgoc.be.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/my")
@RequiredArgsConstructor
public class MyPageController {

    private final OrderService orderService;

    @Operation(summary = "내 주문 목록 조회")
    @GetMapping("/orders")
    public ApiResponse<List<OrderResponse>> getMyOrders() {
        return ApiResponse.success(orderService.getOrdersByUser());
    }

    @Operation(summary = "내 쿠폰함 조회")
    @GetMapping("/coupons")
    public ApiResponse<List<UserCouponResponse>> getMyCoupons() {
        return ApiResponse.success(orderService.getMyCoupons());
    }

    @Operation(summary = "내 주문 상세 조회")
    @GetMapping("/orders/{id}")
    public ApiResponse<OrderResponse> getMyOrderDetail(@PathVariable Long id) {
        String email = SecurityUtil.getCurrentUserEmail();
        return ApiResponse.success(orderService.getOrderDetails(id));
    }
}
