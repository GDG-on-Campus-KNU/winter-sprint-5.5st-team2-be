package gdgoc.be.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
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

    @Min(0)
    @Column(nullable = false)
    private Integer stock;

    @Column(name = "is_available", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean isAvailable;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    public Menu(Long id, Store store, String name, String description, int price, Integer stock, boolean isAvailable, Category category) {
        this.id = id;
        this.store = store;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.isAvailable = isAvailable;
        this.category = category;
    }
    @Builder
    public Menu(Store store, String name, String description, int price, Integer stock, boolean isAvailable, Category category) {
        this.store = store;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.isAvailable = isAvailable;
        this.category = category;
    }

    public static Menu createMenu(Store store, String name, String desc,int price, int stock, Category cat) {
        return Menu.builder()
                .store(store)
                .name(name)
                .description(desc)
                .price(price)
                .stock(stock)
                .isAvailable(stock >0)
                .category(cat)
                .build();
    }
}