  # 리팩토링 진행 현황 보고서

## 1. 엔티티 및 도메인 리팩토링 (완료)
- **User 엔티티**: `phone` 필드 추가 및 정규식 검증(`010-0000-0000`) 적용.
- **Menu에서 Product로 전환**:
    - `Menu` 엔티티를 `Product`로 리네이밍.
    - 추가 필드: `brand`, `imageUrl`, `originalPrice`, `discountRate`, `rating`.
    - 컬렉션 테이블 추가: `product_sizes`, `product_detail_images`, `product_gallery_images`.
- **Repository/Service/Controller**:
    - 모든 `Menu*` 클래스를 `Product*`로 리네이밍.
    - 엔드포인트를 `/api/menus`에서 `/api/products`로 변경.
    - `/api/products/search` API 추가.
- **DTO**:
    - `ProductResponse` 및 `ProductDetailResponse`에 커머스 명세 필드 반영.

## 2. 의존성 업데이트 (완료)
- **장바구니(Cart) 도메인**: `CartItem` 및 `CartService`가 `Product`와 `productId`를 참조하도록 업데이트.
- **주문(Order) 도메인**: `OrderItem` 및 `OrderService`가 `Product`와 `productId`를 참조하도록 업데이트.
- **에러 코드**: `MENU_NOT_FOUND`를 `PRODUCT_NOT_FOUND`로 변경.

## 3. 데이터베이스 스키마 (완료)
- 새로운 `Product` 명세와 `User` 필드에 맞춰 `schema.sql` 및 `data.sql` 업데이트.

---

## 4. 다음 단계 (진행 예정)
- [ ] 유저와 관리자용 회원가입 DTO 분리 (`UserSignupRequest`, `AdminSignupRequest`).
- [ ] 구조화된 로그인 응답 (`refreshToken` 및 `user` 객체 포함).
- [ ] 이메일 중복 확인 API 구현.
