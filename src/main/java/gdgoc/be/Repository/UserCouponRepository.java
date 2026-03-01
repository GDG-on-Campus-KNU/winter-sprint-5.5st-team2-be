package gdgoc.be.Repository;

import gdgoc.be.domain.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.*;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    List<UserCoupon> findByUserEmail(String email);
    
    List<UserCoupon> findByUserEmailAndStatus(String email, UserCoupon.CouponStatus status);

    List<UserCoupon> findByStatus(UserCoupon.CouponStatus status);

    Optional<UserCoupon> findByIdAndUserEmail(Long id, String email);
    Optional<UserCoupon> findByIdAndUserId(Long id, Long userId);

    long countByUserId(Long userId);

    @Modifying
    @Query("UPDATE UserCoupon uc SET uc.status = 'EXPIRED' " +
            "WHERE uc.status = 'ACTIVE' " +
            "AND uc.coupon.expiryDate < :now")
    int expireAllActiveCouponsBefore(@org.springframework.data.repository.query.Param("now") java.time.LocalDateTime now);
}
