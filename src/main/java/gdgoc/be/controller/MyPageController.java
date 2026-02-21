package gdgoc.be.controller;


import gdgoc.be.common.ApiResponse;
import gdgoc.be.dto.OrderResponse;
import gdgoc.be.dto.UserCouponResponse;
import gdgoc.be.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ApiResponse<List<OrderResponse>> getMyOrders(@AuthenticationPrincipal UserDetails userDetails) {
        return ApiResponse.success(orderService.getOrdersByUser());
    }

    @Operation(summary = "내 쿠폰함 조회")
    @GetMapping("/coupons")
    public ApiResponse<List<UserCouponResponse>> getMyCoupons(@AuthenticationPrincipal UserDetails userDetails) {
        return ApiResponse.success(orderService.getMyCoupons());
    }
}
