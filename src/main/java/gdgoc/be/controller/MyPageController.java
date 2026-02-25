package gdgoc.be.controller;

import gdgoc.be.Repository.OrderRepository;
import gdgoc.be.Repository.UserRepository;
import gdgoc.be.common.api.ApiResponse;
import gdgoc.be.dto.order.OrderResponse;
import gdgoc.be.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "MyPage", description = "마이페이지 관련 API")
@RestController
@RequestMapping("/api/my")
@RequiredArgsConstructor
public class MyPageController {

    private final OrderService orderService;

    @Operation(summary = "내 주문 목록 조회", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/orders")
    public ApiResponse<Page<OrderResponse>> getMyOrders(Pageable pageable) {
        return ApiResponse.success(orderService.getMyOrders(pageable));
    }

    @Operation(summary = "내 주문 상세 조회", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/orders/{id}")
    public ApiResponse<OrderResponse> getMyOrderDetail(@PathVariable Long id) {
        return ApiResponse.success(orderService.getOrderDetails(id));
    }
}
