package gdgoc.be.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "menu")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 19, scale = 2)
    private int price;

    // 음수는 입력 금지
    @Min(0)
    @Column(nullable = false)
    private Integer stock;

    @Column(name = "is_available", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean isAvailable;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false) // Assuming category is required based on its usage in the original code.
    private Category category; // DB업데이트 필요.

    public Menu(Store store, String name, String description, int price, Integer stock, boolean isAvailable, Category category) {
        this.store = store;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.isAvailable = isAvailable;
        this.category = category;
    }
}