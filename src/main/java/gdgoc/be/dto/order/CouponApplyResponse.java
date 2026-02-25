package gdgoc.be.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "쿠폰 적용 예상 결과 응답")
public record CouponApplyResponse(
    @Schema(description = "주문 총액")
    BigDecimal totalAmount,
    
    @Schema(description = "할인 금액")
    BigDecimal discountAmount,
    
    @Schema(description = "배송비")
    BigDecimal shippingFee,
    
    @Schema(description = "최종 결제 예정 금액")
    BigDecimal finalAmount,
    
    @Schema(description = "쿠폰 이름")
    String couponName
) {
}
