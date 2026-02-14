package gdgoc.be.domain;


import gdgoc.be.exception.BusinessErrorCode;
import gdgoc.be.exception.BusinessException;
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

    public static CartItem createEmptyCartItem(Long userId, Menu menu) {

        return CartItem.builder()
                .userId(userId)
                .menu(menu)
                .quantity(0)
                .build();
    }

    public void updateQuantityWithStockCheck(int newQuantity) {
        validateStock(newQuantity);
        this.quantity = newQuantity;
    }

    public void addQuantityWithStockCheck(int additionQuantity) {
        int totalQuantity = this.quantity + additionQuantity;
        validateStock(totalQuantity);
        this.quantity = totalQuantity;
    }

    private void validateStock(int targetQuantity) {
        if(this.menu.getStock() < targetQuantity) {
            throw new BusinessException(BusinessErrorCode.OUT_OF_STOCK);
        }
    }
}