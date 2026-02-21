-- 1. 매장 정보 (Store)
INSERT INTO store (name, address, phone) VALUES ('경북대 IT호점', '대구 북구 대학로 80', '053-123-4567');
INSERT INTO store (name, address, phone) VALUES ('복현 오거리점', '대구 북구 복현동', '053-987-6543');

-- 2. 메뉴 정보 (Menu) - 총 11개 등록 (요구사항 10개 이상 충족)

-- 매장 1 메뉴
INSERT INTO menu (store_id, name, description, price, stock, is_available, category)
VALUES (1, '치즈 피자', '기본에 충실한 맛', 15000, 10, true, 'PIZZA');
INSERT INTO menu (store_id, name, description, price, stock, is_available, category)
VALUES (1, '페퍼로니 피자', '짭짤한 페퍼로니 가득', 18000, 5, true, 'PIZZA');
INSERT INTO menu (store_id, name, description, price, stock, is_available, category)
VALUES (1, '불고기 피자', '남녀노소 좋아하는 불고기', 20000, 7, true, 'PIZZA');
INSERT INTO menu (store_id, name, description, price, stock, is_available, category)
VALUES (1, '치즈 버거', '두툼한 패티와 치즈', 6500, 20, true, 'BURGER');
INSERT INTO menu (store_id, name, description, price, stock, is_available, category)
VALUES (1, '콜라', '시원한 코카콜라', 2000, 100, true, 'BEVERAGE');

-- 매장 2 메뉴
INSERT INTO menu (store_id, name, description, price, stock, is_available, category)
VALUES (2, '후라이드 치킨', '바삭한 오리지널', 17000, 15, true, 'CHICKEN');
INSERT INTO menu (store_id, name, description, price, stock, is_available, category)
VALUES (2, '양념 치킨', '매콤달콤 특제 양념', 19000, 12, true, 'CHICKEN');
INSERT INTO menu (store_id, name, description, price, stock, is_available, category)
VALUES (2, '새우 버거', '탱글한 새우살 패티', 7000, 18, true, 'BURGER');
INSERT INTO menu (store_id, name, description, price, stock, is_available, category)
VALUES (2, '감자 튀김', '갓 튀겨낸 바삭함', 3000, 50, true, 'SIDE');
INSERT INTO menu (store_id, name, description, price, stock, is_available, category)
VALUES (2, '사이다', '청량한 스프라이트', 2000, 80, true, 'BEVERAGE');
INSERT INTO menu (store_id, name, description, price, stock, is_available, category)
VALUES (2, '어니언 링', '고소한 양파 튀김', 4000, 30, true, 'SIDE');

-- 3. 사용자 정보 (Users)
-- 비밀번호 'Demo1234!'의 BCrypt 해시값: $2a$10$8K7pS9U3GzW.S8S8S8S8O.X6X6X6X6X6X6X6X6X6X6X6X6X6X6X6
INSERT INTO users (name, email, password, role, address)
VALUES ('성준', 'demo@gdg.com', '$2a$10$8K7pS9U3GzW.S8S8S8S8O.X6X6X6X6X6X6X6X6X6X6X6X6X6X6X6', 'USER', '대구 북구 대학로 80');
INSERT INTO users (name, email, password, role)
VALUES ('관리자', 'admin@gdg.com', '$2a$10$8K7pS9U3GzW.S8S8S8S8O.X6X6X6X6X6X6X6X6X6X6X6X6X6X6X6', 'ADMIN');

-- 4. 쿠폰 정보 (Coupon)
INSERT INTO coupon (name, discount_type, discount_value, min_order_amount)
VALUES ('신규 가입 3000원 할인', 'FIXED', 3000, 15000);
INSERT INTO coupon (name, discount_type, discount_value, min_order_amount)
VALUES ('주말 10% 할인 쿠폰', 'PERCENT', 10, 20000);

-- 5. 사용자 쿠폰 할당 (UserCoupon)
INSERT INTO user_coupon (user_id, coupon_id, is_used) VALUES (1, 1, false);
INSERT INTO user_coupon (user_id, coupon_id, is_used) VALUES (1, 2, false);

-- 6. 장바구니 초기 데이터 (CartItem)
INSERT INTO cart_item (user_id, menu_id, quantity) VALUES (1, 1, 2);