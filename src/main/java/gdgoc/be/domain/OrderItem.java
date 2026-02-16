package gdgoc.be.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
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
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "order_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal orderPrice;

    @Builder
    private OrderItem(Menu menu, int quantity, BigDecimal orderPrice) {
        this.menu = menu;
        this.quantity =quantity;
        this.orderPrice = orderPrice;
    }

    public static OrderItem createOrderItem(Menu menu, int quantity) {

        BigDecimal calculatedPrice = BigDecimal.valueOf(menu.getPrice())
                .multiply(BigDecimal.valueOf(quantity));

        return OrderItem.builder()
                .menu(menu)
                .quantity(quantity)
                .orderPrice(calculatedPrice)
                .build();
    }

}