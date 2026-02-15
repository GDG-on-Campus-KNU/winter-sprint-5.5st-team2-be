package gdgoc.be.service;

import gdgoc.be.domain.Coupon;
import gdgoc.be.domain.OrderItem;
import gdgoc.be.dto.CalculationResult;

import java.math.BigDecimal;
import java.util.List;

public class OrderCalculator {

    // 상수 설정
    private static final BigDecimal FREE_SHIPPING_THRESHOLD = BigDecimal.valueOf(30000); // 3만원
    private static final BigDecimal DEFAULT_SHIPPING_FEE = BigDecimal.valueOf(3000);   // 3천원

    public static CalculationResult calculateTotal(List<OrderItem> items, Coupon coupon) {

        BigDecimal totalAmount = calculateBaseAmount(items);

        BigDecimal discountAmount = BigDecimal.ZERO;
        if(coupon != null) {
            discountAmount = coupon.calculateDiscount(totalAmount);
        }

        BigDecimal amountAfterDiscount = totalAmount.subtract(discountAmount);
        BigDecimal shippingFee = calculateShippingFee(amountAfterDiscount);
        BigDecimal finalAmount = amountAfterDiscount.add(shippingFee);

        return CalculationResult.of(totalAmount, discountAmount, shippingFee, finalAmount);
    }

    private static BigDecimal calculateBaseAmount(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::getOrderPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add); //
    }

    private static BigDecimal calculateShippingFee(BigDecimal amountAfterDiscount) {

        if (amountAfterDiscount.compareTo(FREE_SHIPPING_THRESHOLD) >= 0) {
            return BigDecimal.ZERO;
        }
        return DEFAULT_SHIPPING_FEE;
    }
}