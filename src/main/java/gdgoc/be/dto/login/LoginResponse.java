package gdgoc.be.dto.login;

import lombok.Builder;

@Builder
public record LoginResponse(
    String accessToken,
    String refreshToken,
    UserInfo user
) {
    @Builder
    public record UserInfo(
        Long id,
        String role,
        String name,
        String email
    ) {
    }
}
