package gdgoc.be.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_coupon")
public class UserCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    @Column(name = "is_used", nullable = false)
    private Boolean isUsed;

    @Builder
    private UserCoupon(Long id, User user, Coupon coupon, Boolean isUsed) {
        this.id = id;
        this.user = user;
        this.coupon = coupon;
        this.isUsed = isUsed;
    }

    public static UserCoupon createUserCoupon(User user, Coupon coupon) {
        return UserCoupon.builder()
                .user(user)
                .coupon(coupon)
                .isUsed(false) // 초기 발급 시에는 사용하지 않은 상태로 고정
                .build();
    }

    public boolean isUsed() {
        return this.isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }
}