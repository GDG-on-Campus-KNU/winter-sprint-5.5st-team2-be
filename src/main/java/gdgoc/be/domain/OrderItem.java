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
    private int quantity;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal orderPrice;

    @Builder
    private OrderItem(Product product, int quantity, BigDecimal orderPrice) {
        this.product = product;
        this.quantity = quantity;
        this.orderPrice = orderPrice;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public static OrderItem createOrderItem(Product product, int quantity) {
        BigDecimal calculatedPrice = BigDecimal.valueOf(product.getPrice())
                .multiply(BigDecimal.valueOf(quantity));

        return OrderItem.builder()
                .product(product)
                .quantity(quantity)
                .orderPrice(calculatedPrice)
                .build();
    }
}
