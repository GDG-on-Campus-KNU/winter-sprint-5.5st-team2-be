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

    public CalculationResult calculate(List<OrderItem> items, Long couponId, Long userId) {
        BigDecimal totalAmount = calculateBaseAmount(items);
        Coupon coupon = null;

        if (couponId != null) {
            UserCoupon userCoupon = userCouponRepository.findByIdAndUserId(couponId, userId)
                    .orElseThrow(() -> new BusinessException(BusinessErrorCode.COUPON_NOT_FOUND));

            if (userCoupon.isUsed()) {
                throw new BusinessException(BusinessErrorCode.COUPON_ALREADY_USED);
            }
            coupon = userCoupon.getCoupon();
        }

        BigDecimal discountAmount = BigDecimal.ZERO;
        if (coupon != null) {
            // Coupon 엔티티에 정의된 할인 계산 로직 사용
            discountAmount = coupon.calculateDiscount(totalAmount);
        }

        BigDecimal amountAfterDiscount = totalAmount.subtract(discountAmount);
        BigDecimal shippingFee = calculateShippingFee(amountAfterDiscount);
        BigDecimal finalAmount = amountAfterDiscount.add(shippingFee);

        return CalculationResult.of(totalAmount, discountAmount, shippingFee, finalAmount, coupon);
    }

    private BigDecimal calculateBaseAmount(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::getOrderPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateShippingFee(BigDecimal amountAfterDiscount) {
        if (amountAfterDiscount.compareTo(FREE_SHIPPING_THRESHOLD) >= 0) {
            return BigDecimal.ZERO;
        }
        return DEFAULT_SHIPPING_FEE;
    }
}
