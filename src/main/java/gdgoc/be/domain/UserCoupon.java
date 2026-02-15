package gdgoc.be.domain;

import gdgoc.be.exception.BusinessErrorCode;
import gdgoc.be.exception.BusinessException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
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

    public void validate() {
        // 1. 사용 여부 체크
        if (this.isUsed) {
            throw new BusinessException(BusinessErrorCode.COUPON_ALREADY_USED); // 기존 RuntimeException 대신 커스텀 예외 권장
        }
        // 2. 만료일 체크 (Coupon 엔티티 정보 활용)
        if (this.coupon.getExpiryDate() != null && this.coupon.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException(BusinessErrorCode.COUPON_EXPIRED);
        }
    }
    public void use() {
        this.isUsed = true;
    }
}