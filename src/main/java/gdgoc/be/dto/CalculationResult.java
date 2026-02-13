package gdgoc.be.dto;

import lombok.*;

import java.math.BigDecimal;

@Builder
public record CalculationResult(

        BigDecimal totalAmount,    // 총 상품 금액 (할인 전)
        BigDecimal discountAmount, // 할인 금액
        BigDecimal shippingFee,    // 배송비
        BigDecimal finalAmount
) {

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
