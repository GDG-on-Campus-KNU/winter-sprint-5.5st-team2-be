package gdgoc.be.service;

import gdgoc.be.domain.Role;
import gdgoc.be.domain.User;
import gdgoc.be.dto.login.LoginRequest;
import gdgoc.be.dto.login.LoginResponse;
import gdgoc.be.exception.BusinessErrorCode;
import gdgoc.be.exception.BusinessException;
import gdgoc.be.Repository.UserRepository;
import gdgoc.be.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        // given
        String email = "test@example.com";
        String password = "password123";
        LoginRequest request = new LoginRequest(email, password);
        User user = User.builder()
                .email(email)
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(password, user.getPassword())).willReturn(true);
        given(jwtTokenProvider.createToken(email, user.getRole())).willReturn("mock-token");

        // when
        LoginResponse response = authService.login(request);

        // then
        assertThat(response.accessToken()).isEqualTo("mock-token");
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    void login_fail_user_not_found() {
        // given
        String email = "notfound@example.com";
        LoginRequest request = new LoginRequest(email, "password123");

        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.INVALID_LOGIN_ATTEMPT);
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void login_fail_invalid_password() {
        // given
        String email = "test@example.com";
        String password = "wrongPassword";
        LoginRequest request = new LoginRequest(email, password);
        User user = User.builder()
                .email(email)
                .password("encodedPassword")
                .build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(password, user.getPassword())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.INVALID_LOGIN_ATTEMPT);
    }

    @Test
    @DisplayName("토큰 갱신 성공")
    void refresh_success() {
        // given
        String refreshToken = "valid-refresh-token";
        String email = "test@example.com";
        User user = User.builder()
                .email(email)
                .role(Role.USER)
                .build();
        user.updateRefreshToken(refreshToken);

        given(jwtTokenProvider.validateToken(refreshToken)).willReturn(true);
        given(jwtTokenProvider.getEmail(refreshToken)).willReturn(email);
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(jwtTokenProvider.createToken(email, Role.USER)).willReturn("new-access-token");

        // when
        String newAccessToken = authService.refresh(refreshToken);

        // then
        assertThat(newAccessToken).isEqualTo("new-access-token");
    }

    @Test
    @DisplayName("토큰 갱신 실패 - 유효하지 않은 토큰")
    void refresh_fail_invalid_token() {
        // given
        String refreshToken = "invalid-token";
        given(jwtTokenProvider.validateToken(refreshToken)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.refresh(refreshToken))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.AUTH_INVALID);
    }

    @Test
    @DisplayName("로그아웃 성공")
    void logout_success() {
        // given
        String email = "test@example.com";
        User user = User.builder()
                .email(email)
                .build();
        user.updateRefreshToken("some-refresh-token");

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        // when
        authService.logout(email);

        // then
        assertThat(user.getRefreshToken()).isNull();
    }
}
