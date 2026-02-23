package gdgoc.be.service;

import gdgoc.be.Repository.StoreRepository;
import gdgoc.be.domain.Role;
import gdgoc.be.domain.Store;
import gdgoc.be.domain.User;
import gdgoc.be.dto.*;
import gdgoc.be.dto.login.LoginRequest;
import gdgoc.be.dto.login.LoginResponse;
import gdgoc.be.dto.user.UserResponse;
import gdgoc.be.dto.user.UserSignupRequest;
import gdgoc.be.exception.BusinessErrorCode;
import gdgoc.be.exception.BusinessException;
import gdgoc.be.Repository.UserRepository;
import gdgoc.be.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void signupUser(UserSignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(BusinessErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = User.createUser(
                request.email(),
                passwordEncoder.encode(request.password()),
                request.userName(),
                request.address(),
                request.phone()
        );

        userRepository.save(user);
    }

    @Transactional
    public void signupAdmin(AdminSignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(BusinessErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // Store 생성
        Store store = Store.createStore(request.store(), "", request.phone());
        storeRepository.save(store);

        User admin = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .name("Admin (" + request.store() + ")")
                .phone(request.phone())
                .role(Role.ADMIN)
                .build();

        userRepository.save(admin);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.INVALID_LOGIN_ATTEMPT));

        log.info(user.getEmail());
        log.info(user.getPassword());
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(BusinessErrorCode.INVALID_LOGIN_ATTEMPT);
        }

        String accessToken = jwtTokenProvider.createToken(user.getEmail(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail(), user.getRole());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(LoginResponse.UserInfo.builder()
                        .id(user.getId())
                        .role(user.getRole().name())
                        .name(user.getName())
                        .email(user.getEmail())
                        .build())
                .build();
    }

    public UserResponse getMe(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.USER_NOT_FOUND));
        return UserResponse.from(user);
    }

    public CheckEmailResponse checkEmail(String email) {
        boolean isAvailable = !userRepository.existsByEmail(email);
        return CheckEmailResponse.builder()
                .isAvailable(isAvailable)
                .message(isAvailable ? "사용 가능한 이메일입니다." : "이미 사용 중인 이메일입니다.")
                .build();
    }
}
