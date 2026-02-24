package gdgoc.be.domain;

import gdgoc.be.dto.CalculationResult;
import gdgoc.be.exception.BusinessErrorCode;
import gdgoc.be.exception.BusinessException;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "orders") // Changed from "order" to "orders" based on schema
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "discount_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "delivery_fee", nullable = false, precision = 19, scale = 2)
    private BigDecimal deliveryFee = BigDecimal.ZERO;

    @Column(name = "final_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal finalAmount;

    @Column(name = "coupon_id")
    private Long couponId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private OrderStatus status; // PENDING, COMPLETED, CANCELLED

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PaymentStatus paymentStatus;

    @Column(length = 255)
    private String address;

    @CreationTimestamp
    @Column(name = "order_date", nullable = false, updatable = false)
    private LocalDateTime orderDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();


    // Enum for OrderStatus
    public enum OrderStatus {
        PENDING, COMPLETED, CANCELLED
    }

    public enum PaymentStatus {PAID, UNPAID, CANCELLED}

    @Builder
    private Order(User user, BigDecimal totalAmount, BigDecimal discountAmount,
                  BigDecimal deliveryFee, BigDecimal finalAmount, Long couponId, String address) {
        this.user = user;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.deliveryFee = deliveryFee;
        this.finalAmount = finalAmount;
        this.couponId = couponId;
        this.address = address;
        this.status = OrderStatus.PENDING;
        this.paymentStatus = PaymentStatus.PAID;
    }

    public static Order createOrder(User user, CalculationResult result, Long couponId, String address) {
        return Order.builder()
                .user(user)
                .totalAmount(result.totalAmount())
                .discountAmount(result.discountAmount())
                .deliveryFee(result.shippingFee())
                .finalAmount(result.finalAmount())
                .couponId(couponId)
                .address(address)
                .build();
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void cancel() {
        if (this.status == OrderStatus.CANCELLED) {
            throw new BusinessException(BusinessErrorCode.BAD_REQUEST);
        }
        this.status = OrderStatus.CANCELLED;
        this.paymentStatus = PaymentStatus.CANCELLED;
    }

    public void completePayment() {
        if (this.status == OrderStatus.CANCELLED) {
            throw new BusinessException(BusinessErrorCode.BAD_REQUEST);
        }

        this.status = OrderStatus.COMPLETED;
        this.paymentStatus = PaymentStatus.PAID;
    }
}