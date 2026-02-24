package gdgoc.be.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gdgoc.be.dto.AdminSignupRequest;
import gdgoc.be.dto.CheckEmailResponse;
import gdgoc.be.dto.login.LoginRequest;
import gdgoc.be.dto.login.LoginResponse;
import gdgoc.be.dto.user.UserSignupRequest;
import gdgoc.be.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @Test
    @DisplayName("일반 유저 회원가입 테스트")
    void signupUserTest() throws Exception {
        UserSignupRequest request = new UserSignupRequest(
                "test@example.com",
                "password123",
                "테스트유저",
                "010-1234-5678",
                "서울시 강남구"
        );

        mockMvc.perform(post("/api/auth/signup/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("관리자 회원가입 테스트")
    void signupAdminTest() throws Exception {
        AdminSignupRequest request = new AdminSignupRequest(
                "강남점",
                "010-9876-5432", // 010 형식으로 수정
                "admin@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/auth/signup/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("일반 유저 회원가입 실패 테스트 - 이메일 중복")
    void signupUserDuplicateEmailTest() throws Exception {
        UserSignupRequest request = new UserSignupRequest(
                "duplicate@example.com",
                "password123",
                "테스트유저",
                "010-1234-5678",
                "서울시 강남구"
        );

        // AuthService에서 예외를 던지도록 설정
        doThrow(new gdgoc.be.exception.BusinessException(gdgoc.be.exception.BusinessErrorCode.EMAIL_ALREADY_EXISTS))
                .when(authService).signupUser(any());

        mockMvc.perform(post("/api/auth/signup/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest()); // 혹은 설정된 에러 status
    }

    @Test
    @DisplayName("관리자 회원가입 실패 테스트 - 이메일 중복")
    void signupAdminDuplicateEmailTest() throws Exception {
        AdminSignupRequest request = new AdminSignupRequest(
                "강남점",
                "010-9876-5432", // 010 형식으로 수정
                "duplicate-admin@example.com",
                "password123"
        );

        doThrow(new gdgoc.be.exception.BusinessException(gdgoc.be.exception.BusinessErrorCode.EMAIL_ALREADY_EXISTS))
                .when(authService).signupAdmin(any());

        mockMvc.perform(post("/api/auth/signup/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 테스트")
    void loginTest() throws Exception {
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
                .id(1L)
                .role("USER")
                .name("테스트유저")
                .email("test@example.com")
                .build();
        LoginResponse response = new LoginResponse("access-token", "refresh-token", userInfo);

        given(authService.login(any())).willReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.data.user.id").value(1L))
                .andExpect(jsonPath("$.data.user.role").value("USER"))
                .andExpect(jsonPath("$.data.user.name").value("테스트유저"));
    }

    @Test
    @DisplayName("이메일 중복 확인 테스트")
    void checkEmailTest() throws Exception {
        CheckEmailResponse response = new CheckEmailResponse(true, "사용 가능한 이메일입니다.");

        given(authService.checkEmail("test@example.com")).willReturn(response);

        mockMvc.perform(get("/api/auth/check-email")
                        .param("email", "test@example.com"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isAvailable").value(true));
    }

    @Test
    @DisplayName("토큰 갱신 테스트")
    void refreshTest() throws Exception {
        Map<String, String> request = Map.of("refreshToken", "old-refresh-token");

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").exists());
    }

    @Test
    @DisplayName("로그아웃 테스트")
    void logoutTest() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
