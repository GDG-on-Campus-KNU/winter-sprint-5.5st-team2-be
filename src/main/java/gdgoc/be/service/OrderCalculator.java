package gdgoc.be.service;

import gdgoc.be.domain.OrderItem;
import gdgoc.be.domain.UserCoupon;
import gdgoc.be.dto.CalculationResult;
import gdgoc.be.exception.BusinessErrorCode;
import gdgoc.be.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class OrderCalculator {

    private static final BigDecimal FREE_SHIPPING_THRESHOLD = BigDecimal.valueOf(30000);
    private static final BigDecimal DEFAULT_SHIPPING_FEE = BigDecimal.valueOf(3000);

    public CalculationResult calculate(List<OrderItem> items) {
        // 1. 중복 쿠폰 사용 검증 (하나의 쿠폰은 하나의 상품에만)
        validateDuplicateCoupons(items);

        BigDecimal totalOriginalAmount = items.stream()
                .map(OrderItem::getOriginalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDiscountAmount = items.stream()
                .map(OrderItem::getDiscountAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal amountAfterDiscount = totalOriginalAmount.subtract(totalDiscountAmount);
        BigDecimal shippingFee = calculateShippingFee(amountAfterDiscount);
        BigDecimal finalAmount = amountAfterDiscount.add(shippingFee);

        return CalculationResult.of(totalOriginalAmount, totalDiscountAmount, shippingFee, finalAmount);
    }

    private void validateDuplicateCoupons(List<OrderItem> items) {
        List<Long> appliedCouponIds = items.stream()
                .map(OrderItem::getAppliedCoupon)
                .filter(java.util.Objects::nonNull)
                .map(UserCoupon::getId)
                .toList();

        if (appliedCouponIds.size() != appliedCouponIds.stream().distinct().count()) {
            throw new BusinessException(BusinessErrorCode.BAD_REQUEST); // "하나의 쿠폰은 하나의 상품에만 적용 가능합니다."
        }
    }

    private BigDecimal calculateShippingFee(BigDecimal totalAmount) {
        if (totalAmount.compareTo(FREE_SHIPPING_THRESHOLD) >= 0) {
            return BigDecimal.ZERO;
        }
        return DEFAULT_SHIPPING_FEE;
    }
}
