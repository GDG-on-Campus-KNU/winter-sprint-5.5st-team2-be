package gdgoc.be.dto.user;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MyPageResponse(
        String role,
        long couponCount,
        long orderCount,
        LocalDateTime createdAt
) {
}
