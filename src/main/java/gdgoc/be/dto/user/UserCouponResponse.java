package gdgoc.be.dto.user;

import gdgoc.be.domain.UserCoupon;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UserCouponResponse(
        Long id,
        String couponName,
        String discountType,
        BigDecimal discountValue,
        boolean isUsed,
        LocalDateTime expiryDate
) {

    public static UserCouponResponse from(UserCoupon userCoupon) {
        return new UserCouponResponse(
                userCoupon.getId(),
                userCoupon.getCoupon().getName(),
                userCoupon.getCoupon().getDiscountType().name(),
                userCoupon.getCoupon().getDiscountValue(),
                userCoupon.isUsed(),
                userCoupon.getCoupon().getExpiryDate()
        );
    }
}
