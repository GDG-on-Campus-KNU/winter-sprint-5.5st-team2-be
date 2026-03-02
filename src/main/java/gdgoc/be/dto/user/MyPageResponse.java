package gdgoc.be.dto.user;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MyPageResponse(
        Long id,
        String email,
        String userName,
        String role,
        String phone,
        String address,
        long couponCount,
        long orderCount,
        LocalDateTime createdAt
) {
}
