package gdgoc.be.controller;

import gdgoc.be.common.api.ApiResponse;
import gdgoc.be.common.util.SecurityUtil;
import gdgoc.be.dto.user.UserResponse;
import gdgoc.be.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "사용자 관련 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    @Operation(summary = "내 정보 조회", description = "JWT 토큰을 통해 현재 로그인한 사용자의 정보를 조회합니다.", 
               security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/me")
        public ApiResponse<UserResponse> getMe() {
        String email = SecurityUtil.getCurrentUserEmail();
        UserResponse response = authService.getMe(email);
        return ApiResponse.success(response);
    }
}
