package gdgoc.be.controller;

import gdgoc.be.common.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin", description = "관리자 전용 API")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    @Operation(summary = "관리자 대시보드 조회", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/dashboard")
    public ApiResponse<String> getAdminStats() {
        return ApiResponse.success("관리자 전용 데이터에 접근 성공했습니다.");
    }
}
