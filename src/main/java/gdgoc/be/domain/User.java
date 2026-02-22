package gdgoc.be.domain;


import jakarta.persistence.*;
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
    
        public User(String name, String email, String password, Role role) {
            this.name = name;
            this.email = email;
            this.password = password;
            this.role = role;
        }
    
        @Builder
        private User(String name, String email, String password, Role role, String zipCode, String address, String detailAddress) {
            this.name = name;
            this.email = email;
            this.password = password;
            this.role = (role == null) ? Role.USER : role;
            this.zipCode = zipCode;
            this.address = address;
            this.detailAddress = detailAddress;
        }

    public static User createUser(String email, String password, String name, String address) {
        return User.builder()
                .email(email)
                .password(password)
                .name(name)
                .address(address)
                .role(Role.USER)
                .build();
    }
    }
    