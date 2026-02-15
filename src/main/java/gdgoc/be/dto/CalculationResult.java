package gdgoc.be.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor; // Add this import

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor // Add this annotation
public class CalculationResult {
    private BigDecimal totalAmount;      // 총 상품 금액 (할인 전)
    private BigDecimal discountAmount;   // 할인 금액
    private BigDecimal shippingFee;      // 배송비
    private BigDecimal finalAmount;      // 최종 결제 금액
}