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

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "menu_id", nullable = false)
    private Menu menu;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "selected_size")
    private String selectedSize;

    @Builder
    public CartItem(User user, Menu menu, int quantity, String selectedSize) {
        this.user = user;
        this.menu = menu;
        this.quantity = quantity;
        this.selectedSize = selectedSize;
    }


    public void addQuantity(int amount) {
        this.quantity += amount;
    }

    public void updateItem(int quantity, String selectedSize) {
        validateStock(quantity);
        this.quantity = quantity;
        this.selectedSize = selectedSize;
    }

    public static CartItem createCartItem(User user, Menu menu, int quantity) {
        return CartItem.builder()
                .user(user)
                .menu(menu)
                .quantity(quantity)
                .build();
    }

    public static CartItem createEmptyCartItem(User user, Menu menu, String selectedSize) {

        return CartItem.builder()
                .user(user)
                .menu(menu)
                .quantity(0)
                .selectedSize(selectedSize)
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