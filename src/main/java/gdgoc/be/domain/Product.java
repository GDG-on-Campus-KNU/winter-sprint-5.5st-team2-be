package gdgoc.be.domain;

import gdgoc.be.exception.BusinessErrorCode;
import gdgoc.be.exception.BusinessException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(nullable = false, length = 100)
    private String brand;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private int originalPrice;

    @Column(nullable = false)
    private int discountRate;

    @Column(nullable = false)
    private int price;

    @Min(0)
    @Column(nullable = false)
    private Integer stock;

    @Column(name = "is_available", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean isAvailable;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    @CollectionTable(name = "product_sizes", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "size_option")
    @BatchSize(size = 20)
    private List<String> sizesOptions = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "product_detail_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    @BatchSize(size = 20)
    private List<String> detailImages = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "product_gallery_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    @BatchSize(size = 20)
    private List<String> galleryImages = new ArrayList<>();

    private Double rating;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private Product(Store store, String brand, String name, String imageUrl, int originalPrice, int discountRate,
                   int price, Integer stock, boolean isAvailable, String description,
                   List<String> sizesOptions, List<String> detailImages, List<String> galleryImages,
                   Double rating, Category category) {
        this.store = store;
        this.brand = brand;
        this.name = name;
        this.imageUrl = imageUrl;
        this.originalPrice = originalPrice;
        this.discountRate = discountRate;
        this.price = price;
        this.stock = stock;
        this.isAvailable = isAvailable;
        this.description = description;
        this.sizesOptions = sizesOptions != null ? sizesOptions : new ArrayList<>();
        this.detailImages = detailImages != null ? detailImages : new ArrayList<>();
        this.galleryImages = galleryImages != null ? galleryImages : new ArrayList<>();
        this.rating = rating != null ? rating : 0.0;
        this.category = category;
    }

    public static Product createProduct(Store store, String brand, String name, String imageUrl, int originalPrice,
                                       int discountRate, int stock, Category category, String description) {
        int price = originalPrice * (100 - discountRate) / 100;
        return Product.builder()
                .store(store)
                .brand(brand)
                .name(name)
                .imageUrl(imageUrl)
                .originalPrice(originalPrice)
                .discountRate(discountRate)
                .price(price)
                .stock(stock)
                .isAvailable(stock > 0)
                .category(category)
                .description(description)
                .build();
    }

    public void reduceStock(int quantity) {
        if (this.stock < quantity) {
            throw new BusinessException(BusinessErrorCode.OUT_OF_STOCK);
        }
        this.stock -= quantity;
        if (this.stock == 0) {
            this.isAvailable = false;
        }
    }


    public void addStock(int quantity) {
        this.stock += quantity;
        if (this.stock > 0) {
            this.isAvailable = true;
        }
    }
}
