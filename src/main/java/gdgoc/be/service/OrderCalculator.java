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
        // 1. 총 주문 금액 계산 (할인 전 상품 가격의 합)
        BigDecimal totalAmount = items.stream()
                .map(OrderItem::getOrderPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. 할인 금액 계산
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (coupon != null) {
            // 최소 주문 금액 조건 확인
            if (totalAmount.compareTo(coupon.getMinOrderAmount()) < 0) {
                // 최소 주문 금액 미달 시 할인 미적용 (또는 예외 처리도 가능)
            } else {
                if (coupon.getDiscountType() == Coupon.DiscountType.PERCENT) {
                    discountAmount = totalAmount.multiply(coupon.getDiscountValue())
                            .divide(BigDecimal.valueOf(100), BigDecimal.ROUND_HALF_UP);
                } else if (coupon.getDiscountType() == Coupon.DiscountType.FIXED) {
                    discountAmount = coupon.getDiscountValue();
                }
                // 할인 금액이 총 주문 금액을 초과하지 않도록 보정
                if (discountAmount.compareTo(totalAmount) > 0) {
                    discountAmount = totalAmount;
                }
            }
        }

        // 3. 배송비 계산
        BigDecimal amountAfterDiscount = totalAmount.subtract(discountAmount);
        BigDecimal shippingFee = BigDecimal.ZERO;
        if (amountAfterDiscount.compareTo(FREE_SHIPPING_THRESHOLD) < 0) {
            shippingFee = DEFAULT_SHIPPING_FEE;
        }

        // 4. 최종 결제 금액 계산
        BigDecimal finalAmount = amountAfterDiscount.add(shippingFee);

        return CalculationResult.builder()
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .shippingFee(shippingFee)
                .finalAmount(finalAmount)
                .build();
    }
}