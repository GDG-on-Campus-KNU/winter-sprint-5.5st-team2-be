-- 1. 매장 정보 (Store)
INSERT INTO store (name, address) VALUES ('경북대 IT호점', '대구 북구 대학로 80');
INSERT INTO store (name, address) VALUES ('복현 오거리점', '대구 북구 복현동');

-- 2. 메뉴 정보 (Menu)
-- [주의] store_id가 1인 매장의 메뉴들
INSERT INTO menu (name, price, stock, category, store_id, description) VALUES ('치즈 피자', 15000, 10, 'PIZZA', 1, '기본에 충실한 치즈 피자');
INSERT INTO menu (name, price, stock, category, store_id, description) VALUES ('페퍼로니 피자', 18000, 5, 'PIZZA', 1, '짭짤한 페퍼로니가 가득');
INSERT INTO menu (name, price, stock, category, store_id, description) VALUES ('불고기 피자', 20000, 7, 'PIZZA', 1, '남녀노소 좋아하는 불고기');
INSERT INTO menu (name, price, stock, category, store_id, description) VALUES ('치즈 버거', 6500, 20, 'BURGER', 1, '두툼한 패티와 치즈');
INSERT INTO menu (name, price, stock, category, store_id, description) VALUES ('콜라', 2000, 100, 'BEVERAGE', 1, '시원한 코카콜라');

-- [주의] store_id가 2인 매장의 메뉴들 (메뉴 10개 이상 요구사항 충족)
INSERT INTO menu (name, price, stock, category, store_id, description) VALUES ('후라이드 치킨', 17000, 15, 'CHICKEN', 2, '바삭한 오리지널 치킨');
INSERT INTO menu (name, price, stock, category, store_id, description) VALUES ('양념 치킨', 19000, 12, 'CHICKEN', 2, '매콤달콤한 특제 양념');
INSERT INTO menu (name, price, stock, category, store_id, description) VALUES ('새우 버거', 7000, 18, 'BURGER', 2, '탱글한 새우살 패티');
INSERT INTO menu (name, price, stock, category, store_id, description) VALUES ('감자 튀김', 3000, 50, 'SIDE', 2, '갓 튀겨낸 바삭한 감자');
INSERT INTO menu (name, price, stock, category, store_id, description) VALUES ('사이다', 2000, 80, 'BEVERAGE', 2, '청량한 스프라이트');
INSERT INTO menu (name, price, stock, category, store_id, description) VALUES ('어니언 링', 4000, 30, 'SIDE', 2, '고소한 양파 튀김');

-- 3. 사용자 정보 (Users)
-- 'user'는 SQL 예약어이므로 테이블명을 'users'로 사용하세요
INSERT INTO users (name) VALUES ('성준');
INSERT INTO users (name) VALUES ('테스터');

-- 4. 장바구니 미리 담기 (CartItem)
-- [중요] 반드시 위에서 menu와 users 데이터가 들어간 후에 실행되어야 합니다
-- 유저 1번(성준)이 메뉴 1번(치즈 피자)을 2개 담은 상태
INSERT INTO cart_item (user_id, menu_id, quantity) VALUES (1, 1, 2);