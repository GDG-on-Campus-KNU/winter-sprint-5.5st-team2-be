package gdgoc.be.domain;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "menu_id"})
})
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "menu_id", nullable = false)
    private Menu menu;

    @Column(nullable = false)
    private int quantity;

    @Builder
    public CartItem(Long userId, Menu menu, int quantity) {
        this.userId = userId;
        this.menu = menu;
        this.quantity = quantity;
    }

    public void addQuantity(int amount) {
        this.quantity += amount;
    }

    // 수량 업데이트 메서드
    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }

    public static CartItem createCartItem(Long userId, Menu menu, int quantity) {
        return CartItem.builder()
                .userId(userId)
                .menu(menu)
                .quantity(quantity)
                .build();
    }
}