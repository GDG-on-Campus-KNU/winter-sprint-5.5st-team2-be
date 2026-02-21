package gdgoc.be.Repository;

import gdgoc.be.domain.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    List<UserCoupon> findByUserEmail(String email);

    Optional<UserCoupon> findByIdAndUserEmail(Long id, String email);
    Optional<UserCoupon> findByIdAndUserId(Long id, Long userId);
}