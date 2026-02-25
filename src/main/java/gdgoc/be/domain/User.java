package gdgoc.be.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호는 010-xxxx-xxxx 형식이어야 합니다.")
    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(length = 10)
    private String zipCode;

    @Column(length = 255)
    private String address;

    @Column(length = 255)
    private String detailAddress;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(length = 512)
    private String refreshToken;

    public void updateRefreshToken(String rawToken) {
        if (rawToken == null) {
            this.refreshToken = null;
            return;
        }
        this.refreshToken = hashToken(rawToken);
    }

    public boolean isValidRefreshToken(String rawToken) {
        if (rawToken == null || this.refreshToken == null) return false;
        return this.refreshToken.equals(hashToken(rawToken));
    }

    private String hashToken(String token) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("토큰 해싱 중 오류가 발생했습니다.", e);
        }
    }

    public User(String name, String email, String password, Role role, String phone) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.phone = phone;
    }

    @Builder
    private User(String name, String email, String password, Role role, String phone, String zipCode, String address, String detailAddress) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = (role == null) ? Role.USER : role;
        this.phone = phone;
        this.zipCode = zipCode;
        this.address = address;
        this.detailAddress = detailAddress;
    }

    public static User createUser(String email, String password, String name, String address, String phone) {
        return User.builder()
                .email(email)
                .password(password)
                .name(name)
                .address(address)
                .phone(phone)
                .role(Role.USER)
                .build();
    }
}
    