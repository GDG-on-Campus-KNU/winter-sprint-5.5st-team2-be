package gdgoc.be.service;

import gdgoc.be.Repository.UserCouponRepository;
import gdgoc.be.domain.UserCoupon;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponScheduler {

    private final UserCouponRepository userCouponRepository;

    /**
     * 매시간 0분에 실행되어 만료된 쿠폰의 상태를 EXPIRED로 변경합니다.
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void expireCoupons() {
        log.info("쿠폰 만료 스케줄러 실행 시작 - {}", LocalDateTime.now());

        List<UserCoupon> activeCoupons = userCouponRepository.findByStatus(UserCoupon.CouponStatus.ACTIVE);
        
        long expiredCount = activeCoupons.stream()
                .filter(userCoupon -> userCoupon.getCoupon().getExpiryDate() != null &&
                        userCoupon.getCoupon().getExpiryDate().isBefore(LocalDateTime.now()))
                .peek(UserCoupon::expire)
                .count();

        log.info("쿠폰 만료 스케줄러 실행 완료. 총 {}개의 쿠폰이 만료 처리되었습니다.", expiredCount);
    }
}
