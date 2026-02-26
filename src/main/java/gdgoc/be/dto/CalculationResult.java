package gdgoc.be.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CalculationResult(
        BigDecimal totalAmount,
        BigDecimal discountAmount,
        BigDecimal shippingFee,
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
