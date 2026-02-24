package gdgoc.be.dto.user;

import gdgoc.be.domain.User;
import lombok.Builder;

@Builder
public record UserResponse(
        Long id,
        String email,
        String userName,
        String role,
        String phone,
        String address
) {
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .userName(user.getName())
                .role(user.getRole().name())
                .phone(user.getPhone())
                .address(user.getAddress())
                .build();
    }
}
