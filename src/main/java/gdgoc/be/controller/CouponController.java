package gdgoc.be.controller;

import gdgoc.be.common.api.ApiResponse;
import gdgoc.be.dto.order.CouponApplyRequest;
import gdgoc.be.dto.order.CouponApplyResponse;
import gdgoc.be.dto.user.UserCouponResponse;
import gdgoc.be.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Coupon", description = "쿠폰 관련 API")
@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final OrderService orderService;

    @Operation(summary = "내 쿠폰함 조회", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/me")
    public ApiResponse<List<UserCouponResponse>> getMyCoupons() {
        return ApiResponse.success(orderService.getMyCoupons());
    }

    @Operation(summary = "쿠폰 적용 예상 결과 조회", description = "주문 생성 전 쿠폰 적용 시 할인 금액과 최종 금액을 미리 계산합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/{couponId}/apply")
    public ApiResponse<CouponApplyResponse> applyCoupon(
            @PathVariable Long couponId,
            @RequestBody CouponApplyRequest request) {
        return ApiResponse.success(orderService.applyCoupon(couponId, request));
    }
}
