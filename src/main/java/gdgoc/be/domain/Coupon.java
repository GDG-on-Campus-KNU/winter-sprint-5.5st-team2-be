package gdgoc.be.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "coupon")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 20)
    private DiscountType discountType; // PERCENT, FIXED

    @Column(name = "discount_value", nullable = false, precision = 19, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "min_order_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal minOrderAmount = BigDecimal.ZERO;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    public enum DiscountType {
        PERCENT, FIXED
    }

    @Builder
    private Coupon(String name, DiscountType discountType, BigDecimal discountValue,
                   BigDecimal minOrderAmount, LocalDateTime expiryDate) {
        this.name = name;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minOrderAmount = (minOrderAmount != null) ? minOrderAmount : BigDecimal.ZERO;
        this.expiryDate = expiryDate;
    }

    public static Coupon createCoupon(String name, DiscountType type, BigDecimal value,
                                      BigDecimal minAmount, LocalDateTime expiry) {

        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("할인 값은 0보다 커야 합니다.");
        }

        return Coupon.builder()
                .name(name)
                .discountType(type)
                .discountValue(value)
                .minOrderAmount(minAmount)
                .expiryDate(expiry)
                .build();
    }

    public BigDecimal calculateDiscount(BigDecimal totalAmount) {

        if (totalAmount.compareTo(this.minOrderAmount) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount = (this.discountType == DiscountType.PERCENT)
                ? calculatePercentDiscount(totalAmount)
                : this.discountValue;

        return discount.min(totalAmount);
    }

    private BigDecimal calculatePercentDiscount(BigDecimal totalAmount) {
        return totalAmount.multiply(this.discountValue)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}