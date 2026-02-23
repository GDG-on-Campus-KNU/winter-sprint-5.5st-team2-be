package gdgoc.be.controller;

import gdgoc.be.common.ApiResponse;
import gdgoc.be.dto.*;
import gdgoc.be.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Authentication", description = "회원가입 및 로그인 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "일반 유저 회원가입", description = "새로운 일반 사용자를 등록합니다.")
    @PostMapping("/signup/user")
    public ApiResponse<Void> signupUser(@Valid @RequestBody UserSignupRequest request) {
        authService.signupUser(request);
        return ApiResponse.success(null);
    }

    @Operation(summary = "관리자 회원가입", description = "새로운 관리자를 등록합니다.")
    @PostMapping("/signup/admin")
    public ApiResponse<Void> signupAdmin(@Valid @RequestBody AdminSignupRequest request) {
        authService.signupAdmin(request);
        return ApiResponse.success(null);
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하여 토큰을 발급받습니다.")
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.success(response);
    }

    @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 통해 액세스 토큰을 갱신합니다.")
    @PostMapping("/refresh")
    public ApiResponse<Map<String, String>> refresh(@RequestBody Map<String, String> request) {
        // TODO: Implement refresh token logic
        return ApiResponse.success(Map.of("accessToken", "new-access-token"));
    }

    @Operation(summary = "로그아웃", description = "현재 세션을 로그아웃합니다.")
    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        return ApiResponse.success(null);
    }

    @Operation(summary = "이메일 중복 확인", description = "해당 이메일이 사용 가능한지 확인합니다.")
    @GetMapping("/check-email")
    public ApiResponse<CheckEmailResponse> checkEmail(@RequestParam String email) {
        return ApiResponse.success(authService.checkEmail(email));
    }
}
