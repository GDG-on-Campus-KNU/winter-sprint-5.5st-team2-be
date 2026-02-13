## 🛠 1. 기술 스택 및 환경 (Core Stack)

| **항목** | **설정값** | **비고** |
| --- | --- | --- |
| **Java Version** | `21` | 가상 스레드(Virtual Threads) 및 패턴 매칭 활용 가능 |
| **Spring Boot** | `3.4.2` | 최신 사양 및 Jakarta EE 기준 준수 |
| **Build Tool** | `Gradle - Groovy` | `build.gradle` 형식 사용 |
| **Packaging** | `Jar` | - |
| **Configuration** | `application.properties` | YAML 대신 Properties 형식을 우선 사용 |

## 📦 2. 데이터 및 인프라 설정 (Dependencies)

| 항목 | 비고 |
| --- | --- |
| H2 Database | 로컬 개발 및 테스트를 위한 인메모리 DB로 활용합니다. |
| Spring Data JPA | 객체 지향적인 도메인 모델링과 복잡한 관계 매핑에 사용합니다 (Hibernate 기반). |
| Spring Data JDBC | 복잡한 ORM 설정 없이 단순한 매핑이나 성능 최적화가 필요한 쿼리에 선택적으로 활용합니다. |
| Spring Web | Apache Tomcat 기반의 RESTful API를 구축하며 Spring MVC 패턴을 준수합니다. |

## 📂 3. Gradle

```java
plugins {
	id 'java'
	id 'org.springframework.boot' version '3.4.2' // 4.0.2에서 안정적인 3.4.2로 변경
	id 'io.spring.dependency-management' version '1.1.7'
}

group = 'gdgoc'
version = '0.0.1-SNAPSHOT'
description = 'Demo project for Spring Boot'

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

configurations {
	compileOnly {
		extendsFrom annotationProcessor
	}
}

repositories {
	mavenCentral()
}
dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
	// 명칭 수정: spring-boot-starter-session-jdbc -> spring-session-jdbc
	implementation 'org.springframework.session:spring-session-jdbc'
	implementation 'org.springframework.boot:spring-boot-starter-web'
	implementation 'org.springframework.boot:spring-boot-starter-validation'

	// Swagger
	implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.4'

	compileOnly 'org.projectlombok:lombok'
	runtimeOnly 'com.h2database:h2'
	annotationProcessor 'org.projectlombok:lombok'

	testImplementation 'org.springframework.boot:spring-boot-starter-test'
	testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}
tasks.named('test') {
	useJUnitPlatform()
}

```

## 📂 4. GDGOC Project  초기구조

```
							       																		       
gdgoc.be
├── common                      # 공통 모듈 및 유틸리티
│   ├── api                     # 공통 응답 규격 (ApiResponse)
│   ├── exception               # 예외 처리 (GlobalExceptionHandler)
│   └── util                    # 공통 유틸리티
│
├── config                      # 프로젝트 설정
│   ├── web                     # HandlerMethodArgumentResolver (X-USER-ID 처리)
│   └── jpa                     # JPA 및 JDBC 영속성 설정
│
├── domain                      # 도메인 모델 (Entity, Repository)
│   ├── user                    # 사용자 (User)
│   ├── store                   # 매장 (Store)
│   ├── menu                    # 메뉴 및 재고 관리 (Menu)
│   ├── cart                    # 장바구니 (CartItem - 복합 유니크 제약 포함)
│   ├── order                   # 주문 및 상세 (Order, OrderItem)
│   └── coupon                  # 쿠폰 (Coupon, UserCoupon)
│
├── application                 # 비즈니스 서비스 레이어
│   ├── cart                    # 장바구니 비즈니스 로직
│   ├── order                   # 주문 생성 및 트랜잭션 관리 (@Transactional)
│   └── price                   # 금액 계산 모듈 (PriceCalculator-무료 배송,쿠폰 계산)
│
└── web                         # 외부 인터페이스 레이어 (Controller, DTO)
    ├── controller              # API 엔드포인트 (Menu, Cart, Order)
    └── dto                     # Request/Response DTO (Java 21 record 활용 권장)

src/main/resources
├── application.properties      # 프로젝트 환경 설정 (Properties 형식)
└── db
    ├── schema.sql              # 테이블 설계 및 제약 조건 (Unique, Check 등)
    └── data.sql                # 시드 데이터 (매장 2개, 메뉴 10개 이상, 샘플 유저)
```

## 📂 5. commit 컨벤션

- feat : 새로운 기능 추가
- fix : 버그 수정
- style : 코드 포맷 등 스타일 변경
- test : 테스트 코드
- init : 초기 설정
- refactor : 구조 변경

## 📋 6. PR(Pull Request) 템플릿

```markdown
## 📌 변경 사항
- [feat] 로그인 API 구현
- [refactor] User DTO 구조 변경

## 📸 스크린샷 (UI 변경 시)

## 🧐 구현 방식 & 이유 (선택)
- JWT 라이브러리로 jjwt를 사용했습니다. 이유는...

## ✅ 체크리스트
- [ ] 컨벤션 준수
- [ ] 테스트 코드 실행 확인
```

## 🗄️ Database Schema (`schema.sql`)

```sql
-- 1. User (사용자)
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Store (매장)
CREATE TABLE store (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    phone VARCHAR(50)
);

-- 3. Menu (메뉴)
CREATE TABLE menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(19, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    is_available BOOLEAN DEFAULT TRUE,
    CONSTRAINT chk_menu_stock CHECK (stock >= 0), -- 재고 음수 입력 방지
    FOREIGN KEY (store_id) REFERENCES store(id)
);

-- 4. CartItem (장바구니)
CREATE TABLE cart_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    UNIQUE (user_id, menu_id), -- 중복 담기 방지 제약
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (menu_id) REFERENCES menu(id)
);

-- 5. Orders (주문 메인)
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(19, 2) NOT NULL,
    discount_amount DECIMAL(19, 2) DEFAULT 0,
    delivery_fee DECIMAL(19, 2) DEFAULT 0,
    final_amount DECIMAL(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL, -- PENDING, COMPLETED, CANCELLED
    address VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 6. OrderItem (주문 상세)
CREATE TABLE order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    order_price DECIMAL(19, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (menu_id) REFERENCES menu(id)
);

-- 7. Coupon (쿠폰 기초)
CREATE TABLE coupon (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    discount_type VARCHAR(20) NOT NULL, -- PERCENT, FIXED
    discount_value DECIMAL(19, 2) NOT NULL,
    min_order_amount DECIMAL(19, 2) DEFAULT 0
);

-- 8. UserCoupon (사용자 보유 쿠폰)
CREATE TABLE user_coupon (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    coupon_id BIGINT NOT NULL,
    is_used BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (coupon_id) REFERENCES coupon(id)
);
```
