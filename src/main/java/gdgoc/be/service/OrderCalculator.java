package gdgoc.be.service;

import gdgoc.be.Repository.UserCouponRepository;
import gdgoc.be.domain.Coupon;
import gdgoc.be.domain.OrderItem;
import gdgoc.be.domain.UserCoupon;
import gdgoc.be.dto.CalculationResult;
import gdgoc.be.exception.BusinessErrorCode;
import gdgoc.be.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderCalculator {

    private final UserCouponRepository userCouponRepository;

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
        BigDecimal shippingFee = calculateShippingFee(totalOriginalAmount);
        BigDecimal finalAmount = amountAfterDiscount.add(shippingFee);

        // CalculationResult 구조상 단일 Coupon만 반환 가능하므로, 
        // 여기서는 대표 쿠폰 혹은 정보를 null로 처리하거나 DTO를 확장해야 함.
        // 우선 하위 호환성을 위해 null로 전달 (필요시 CalculationResult 수정)
        return CalculationResult.of(totalOriginalAmount, totalDiscountAmount, shippingFee, finalAmount, null);
    }

    private void validateDuplicateCoupons(List<OrderItem> items) {
        long couponCount = items.stream()
                .map(OrderItem::getAppliedCoupon)
                .filter(java.util.Objects::nonNull)
                .map(UserCoupon::getId)
                .count();

        long uniqueCouponCount = items.stream()
                .map(OrderItem::getAppliedCoupon)
                .filter(java.util.Objects::nonNull)
                .map(UserCoupon::getId)
                .distinct()
                .count();

        if (couponCount != uniqueCouponCount) {
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
