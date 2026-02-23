package gdgoc.be.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "cart_item",
        uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "product_id"})
}
)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Builder
    public CartItem(User user, Product product, int quantity) {
        this.user = user;
        this.product = product;
        this.quantity = quantity;
    }

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

    public static CartItem createCartItem(User user, Product product, int quantity) {
        return CartItem.builder()
                .user(user)
                .product(product)
                .quantity(quantity)
                .build();
    }

    public static CartItem createEmptyCartItem(User user, Product product) {
        return CartItem.builder()
                .user(user)
                .product(product)
                .quantity(0)
                .build();
    }

    public void validateStock(int targetQuantity) {
        if(this.product.getStock() < targetQuantity) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
    }
}
