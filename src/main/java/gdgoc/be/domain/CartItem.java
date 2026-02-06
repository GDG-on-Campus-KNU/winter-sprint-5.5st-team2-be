package gdgoc.be.domain;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "menu_id"}) // [중요] 중복 담기 방지 제약
})
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId; // 임시 X-Userx-ID 식별용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "menu_id", nullable = false)
    private Menu menu;

    @Column(nullable = false)
    private int quantity;

    // 수량 증가 메서드
    public void addQuantity(int amount) {
        this.quantity += amount;
    }

    // 수량 업데이트 메서드
    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }
}
