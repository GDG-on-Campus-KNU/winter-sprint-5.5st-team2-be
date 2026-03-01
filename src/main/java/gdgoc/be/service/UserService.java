package gdgoc.be.service;

import gdgoc.be.Repository.OrderRepository;
import gdgoc.be.Repository.UserCouponRepository;
import gdgoc.be.Repository.UserRepository;
import gdgoc.be.domain.User;
import gdgoc.be.dto.user.MyPageResponse;
import gdgoc.be.exception.BusinessErrorCode;
import gdgoc.be.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserCouponRepository userCouponRepository;
    private final OrderRepository orderRepository;

    public MyPageResponse getMyPageInfo(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.USER_NOT_FOUND));

        long couponCount = userCouponRepository.countByUserId(user.getId());
        long orderCount = orderRepository.countByUserId(user.getId());

        return MyPageResponse.builder()
                .role(user.getRole().name())
                .couponCount(couponCount)
                .orderCount(orderCount)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
