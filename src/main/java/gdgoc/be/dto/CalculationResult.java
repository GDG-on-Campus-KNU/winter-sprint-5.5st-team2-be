package gdgoc.be.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class CalculationResult {
    private BigDecimal totalAmount;      // 총 상품 금액 (할인 전)
    private BigDecimal discountAmount;   // 할인 금액
    private BigDecimal shippingFee;      // 배송비
    private BigDecimal finalAmount;// 최종 결제 금액


    @Builder
    public CalculationResult(BigDecimal totalAmount, BigDecimal discountAmount, BigDecimal finalAmount, BigDecimal shippingFee) {
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.shippingFee = shippingFee;
    }

    public static CalculationResult of(BigDecimal totalAmount, BigDecimal discountAmount,
                                       BigDecimal shippingFee, BigDecimal finalAmount) {
        return CalculationResult.builder()
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .shippingFee(shippingFee)
                .finalAmount(finalAmount)
                .build();
    }
}
