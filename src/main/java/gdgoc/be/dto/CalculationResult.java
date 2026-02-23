package gdgoc.be.dto;

import gdgoc.be.domain.Coupon;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CalculationResult(
        BigDecimal totalAmount,
        BigDecimal discountAmount,
        BigDecimal shippingFee,
        BigDecimal finalAmount,
        Coupon coupon
) {
    public static CalculationResult of(BigDecimal totalAmount, BigDecimal discountAmount,
                                       BigDecimal shippingFee, BigDecimal finalAmount, Coupon coupon) {
        return CalculationResult.builder()
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .shippingFee(shippingFee)
                .finalAmount(finalAmount)
                .coupon(coupon)
                .build();
    }
}
