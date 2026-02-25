# 📝 프로젝트 보안 및 아키텍처 개선 결과 보고서

**일자:** 2026년 2월 25일  
**대상:** Backend API 서버 (`winter-sprint-5.5st-team2-be`)  
**주요 내용:** 보안 취약점 해결, 아키텍처 레이어 정립, 비즈니스 로직 오류 수정 및 성능 최적화

---

## 🚀 1. 보안 강화 (Security)

### 1.1 리프레시 토큰 검증 로직 보안 강화
*   **기존 문제:** DB에 해싱되어 저장된 리프레시 토큰을 서비스 레이어에서 평문 토큰과 직접 `equals()`로 비교하여 검증에 실패하거나 보안상 취약할 수 있는 구조였습니다.
*   **수정 사항:** `User` 엔티티 내부에 `isValidRefreshToken(String rawToken)` 메서드를 구현하여, 입력받은 평문 토큰을 다시 해싱한 뒤 DB의 값과 비교하도록 수정했습니다.
*   **관련 파일:** `User.java`, `AuthService.java`

### 1.2 보안 유틸리티 안전성 및 현대화
*   **기존 문제:** `SecurityUtil.getPrincipal().toString()` 호출 시 익명 사용자나 비정상적인 인증 상태에서 런타임 에러가 발생할 위험이 있었습니다.
*   **수정 사항:** Java 21의 **Pattern Matching for instanceof**를 적용하여 타입을 명확히 확인하고, 인증 정보가 없을 경우 `BusinessException(AUTH_REQUIRED)`을 명시적으로 던지도록 강화했습니다.
*   **관련 파일:** `SecurityUtil.java`

---

## 🏛️ 2. 아키텍처 및 레이어 설계 (Architecture)

### 2.1 컨트롤러 내 Repository 직접 주입 제거
*   **기존 문제:** `MyPageController` 등 일부 컨트롤러에 `UserRepository`, `OrderRepository`가 직접 주입되어 레이어 간 관심사 분리(SoC) 원칙을 위반하고 있었습니다.
*   **수정 사항:** 사용하지 않는 Repository 주입을 제거하고, 모든 도메인 로직은 서비스 레이어(`OrderService` 등)를 거치도록 정립했습니다.
*   **관련 파일:** `MyPageController.java`

### 2.2 도메인 객체 내 부수 효과(Side Effect) 제거
*   **기존 문제:** `UserCoupon.validate()` 호출 시 내부적으로 `expire()`를 호출하여 DB 상태를 변경하는 사이드 이펙트가 발생했습니다. 이는 조회성 검증 로직에서 예상치 못한 쓰기 작업을 유발합니다.
*   **수정 사항:** `validate()`는 유효성 체크와 예외 투척만 담당하도록 역할을 제한했습니다. 상태 변경은 스케줄러나 명시적인 비즈니스 메서드에서만 수행됩니다.
*   **관련 파일:** `UserCoupon.java`

---

## ⚙️ 3. 비즈니스 로직 및 성능 최적화 (Business Logic & Performance)

### 3.1 배송비 계산 로직 오류 수정
*   **기존 문제:** 무료 배송 판정 기준이 '할인 전 금액'으로 설정되어 있어, 실 결제 금액이 기준 미달임에도 배송비가 면제되는 로직 오류가 있었습니다.
*   **수정 사항:** 사용자가 실제로 지불하는 **'할인 적용 후 금액(amountAfterDiscount)'**을 기준으로 배송비가 책정되도록 수정했습니다.
*   **관련 파일:** `OrderCalculator.java`

### 3.2 쿠폰 만료 스케줄러 성능 개선 (N+1 문제 해결)
*   **기존 문제:** 모든 'ACTIVE' 쿠폰을 메모리에 로드한 뒤 루프를 돌며 개별적으로 업데이트를 수행하여, 데이터 증가 시 심각한 성능 저하가 우려되었습니다.
*   **수정 사항:** `UserCouponRepository`에 `@Modifying`과 `@Query`를 사용한 벌크 업데이트 메서드를 추가하여, 단일 SQL로 만료 처리가 완료되도록 최적화했습니다.
*   **관련 파일:** `UserCouponRepository.java`, `CouponScheduler.java`

### 3.3 중복 쿠폰 검증 효율화
*   **기존 문제:** 동일한 아이템 리스트를 두 번 스트림 순회하여 쿠폰 개수와 중복 여부를 체크했습니다.
*   **수정 사항:** ID 리스트를 한 번만 추출한 뒤 `size()`와 `distinct().count()`를 비교하는 방식으로 효율화했습니다.
*   **관련 파일:** `OrderCalculator.java`

---

## 💎 4. 코드 품질 및 스타일 (Code Quality)

### 4.1 Java 21 및 최신 컨벤션 적용
*   **수정 사항:** 가독성이 낮고 불필요하게 장황한 `Collectors.toList()`를 모두 Java 16+ 스타일인 **`.toList()`**로 교체하여 불변 리스트를 반환하고 가독성을 높였습니다.
*   **관련 파일:** `OrderService.java`, `ProductService.java` 등 프로젝트 전반

### 4.2 쿠폰 적용 메시지 동적화
*   **수정 사항:** `CouponApplyResponse` 생성 시 "복수 쿠폰 적용됨"이라는 하드코딩된 메시지 대신, 실제로 적용된 쿠폰들의 이름을 나열(예: "첫 주문 할인, 주말 쿠폰 적용됨")하도록 동적 로직을 추가했습니다.
*   **관련 파일:** `OrderService.java`

### 4.3 기타 정리 사항
*   도메인 엔티티 내 불필요한 `@Setter` 제거 및 캡슐화 확인.
*   사용되지 않는 임포트(`java.util.stream.Collectors`) 및 임시 주석 정리.
*   모든 소스 파일 끝에 개행(New Line) 추가 준수.
