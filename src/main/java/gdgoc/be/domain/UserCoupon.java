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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CouponStatus status;

    public enum CouponStatus {
        ACTIVE, USED, EXPIRED
    }

    @Builder
    private UserCoupon(Long id, User user, Coupon coupon, CouponStatus status) {
        this.id = id;
        this.user = user;
        this.coupon = coupon;
        this.status = status;
    }

    public static UserCoupon createUserCoupon(User user, Coupon coupon) {
        return UserCoupon.builder()
                .user(user)
                .coupon(coupon)
                .status(CouponStatus.ACTIVE)
                .build();
    }

    public boolean isUsed() {
        return this.status == CouponStatus.USED;
    }

    public void validate() {
        // 1. 상태 체크
        if (this.status == CouponStatus.USED) {
            throw new BusinessException(BusinessErrorCode.COUPON_ALREADY_USED);
        }
        if (this.status == CouponStatus.EXPIRED) {
            throw new BusinessException(BusinessErrorCode.COUPON_EXPIRED);
        }
        // 2. 실시간 만료일 체크 (상태가 아직 ACTIVE여도 시간이 지났을 수 있으므로)
        if (this.coupon.getExpiryDate() != null && this.coupon.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException(BusinessErrorCode.COUPON_EXPIRED);
        }
    }

    public void use() {
        if (this.status != CouponStatus.ACTIVE) {
            throw new BusinessException(BusinessErrorCode.BAD_REQUEST);
        }
        this.status = CouponStatus.USED;
    }

    public void expire() {
        this.status = CouponStatus.EXPIRED;
    }
}