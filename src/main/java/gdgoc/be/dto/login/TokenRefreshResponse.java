package gdgoc.be.dto.login;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "토큰 갱신 응답")
public record TokenRefreshResponse(
    @Schema(description = "새로운 액세스 토큰")
    String accessToken
) {
}
