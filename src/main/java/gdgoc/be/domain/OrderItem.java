package gdgoc.be.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal orderPrice; // 할인이 적용된 최종 금액

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal originalPrice; // 할인 전 원가

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal discountAmount; // 이 상품에서 깎인 금액

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_coupon_id")
    private UserCoupon appliedCoupon;

    @Column(nullable = false)
    private String selectedSize;

    @Builder
    private OrderItem(Product product, int quantity, BigDecimal orderPrice, BigDecimal originalPrice, BigDecimal discountAmount, UserCoupon appliedCoupon, String selectedSize) {
        this.product = product;
        this.quantity = quantity;
        this.orderPrice = orderPrice;
        this.originalPrice = originalPrice;
        this.discountAmount = discountAmount;
        this.appliedCoupon = appliedCoupon;
        this.selectedSize = selectedSize;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public static OrderItem createOrderItem(Product product, int quantity, String selectedSize, UserCoupon userCoupon) {
        BigDecimal basePrice = BigDecimal.valueOf(product.getPrice())
                .multiply(BigDecimal.valueOf(quantity));
        
        BigDecimal discount = BigDecimal.ZERO;
        
        if (userCoupon != null) {
            userCoupon.validate();
            discount = userCoupon.getCoupon().calculateDiscount(basePrice);
        }

        return OrderItem.builder()
                .product(product)
                .quantity(quantity)
                .originalPrice(basePrice)
                .discountAmount(discount)
                .orderPrice(basePrice.subtract(discount))
                .appliedCoupon(userCoupon)
                .selectedSize(selectedSize)
                .build();
    }
}
