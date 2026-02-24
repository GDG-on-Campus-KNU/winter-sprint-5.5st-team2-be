-- 1. 사용자 정보 (User) - 총 3명 (USER 2, ADMIN 1)
-- 비밀번호는 'password123' BCrypt 인코딩 값 (예시)
INSERT INTO users (name, email, password, phone, role, zip_code, address, detail_address)
VALUES ('홍길동', 'user1@example.com', '$2a$10$7pEXL8tKIsD3EunOskrKquC3/9T8/N9f2B2wU0h7g5O5Y7fI3Nf1S', '010-1234-5678', 'USER', '12345', '서울시 강남구', '테헤란로 123');

INSERT INTO users (name, email, password, phone, role, zip_code, address, detail_address)
VALUES ('김철수', 'user2@example.com', '$2a$10$7pEXL8tKIsD3EunOskrKquC3/9T8/N9f2B2wU0h7g5O5Y7fI3Nf1S', '010-9876-5432', 'USER', '54321', '부산시 해운대구', '해운대해변로 456');

INSERT INTO users (name, email, password, phone, role, zip_code, address, detail_address)
VALUES ('관리자', 'admin@example.com', '$2a$10$7pEXL8tKIsD3EunOskrKquC3/9T8/N9f2B2wU0h7g5O5Y7fI3Nf1S', '010-0000-0000', 'ADMIN', '00000', '서울시 본사', '관리실');

-- 2. 상점 정보 (Store)
INSERT INTO store (name, address, phone) VALUES ('강남점', '서울시 강남구', '02-123-4567');
INSERT INTO store (name, address, phone) VALUES ('온라인몰', '서울특별시 본사', '080-123-4567');

-- 3. 상품 정보 (Product)
-- 브랜드, 이미지URL, 정가, 할인율, 최종가 등 명세 반영
INSERT INTO products (store_id, brand, name, image_url, original_price, discount_rate, price, stock, is_available, description, rating, category)
VALUES (2, 'Nike', 'Air Jordan 1 Retro High OG', 'https://images.example.com/nike/aj1.jpg', 200000, 10, 180000, 50, TRUE, 'The classic AJ1 in High OG silhouette.', 4.8, 'CHICKEN'); -- CHICKEN은 임시 카테고리 (필요시 Category enum 수정)

INSERT INTO products (store_id, brand, name, image_url, original_price, discount_rate, price, stock, is_available, description, rating, category)
VALUES (2, 'Adidas', 'Yeezy Boost 350 V2', 'https://images.example.com/adidas/yeezy350.jpg', 300000, 0, 300000, 20, TRUE, 'Iconic Yeezy Boost 350 V2 in Beluga colorway.', 4.9, 'CHICKEN');

INSERT INTO products (store_id, brand, name, image_url, original_price, discount_rate, price, stock, is_available, description, rating, category)
VALUES (2, 'Samsung', 'Galaxy S24 Ultra', 'https://images.example.com/samsung/s24u.jpg', 1600000, 5, 1520000, 100, TRUE, 'Samsung Galaxy S24 Ultra with AI features.', 4.7, 'BURGER');

INSERT INTO products (store_id, brand, name, image_url, original_price, discount_rate, price, stock, is_available, description, rating, category)
VALUES (2, 'Apple', 'iPhone 15 Pro', 'https://images.example.com/apple/iphone15pro.jpg', 1500000, 0, 1500000, 80, TRUE, 'iPhone 15 Pro with Titanium build.', 4.9, 'BURGER');

-- 상품 옵션 및 이미지 샘플
INSERT INTO product_sizes (product_id, size_option) VALUES (1, '260'), (1, '270'), (1, '280');
INSERT INTO product_sizes (product_id, size_option) VALUES (2, '255'), (2, '265'), (2, '275');

INSERT INTO product_detail_images (product_id, image_url) VALUES (1, 'https://images.example.com/nike/aj1_detail1.jpg'), (1, 'https://images.example.com/nike/aj1_detail2.jpg');
INSERT INTO product_gallery_images (product_id, image_url) VALUES (1, 'https://images.example.com/nike/aj1_gallery1.jpg'), (1, 'https://images.example.com/nike/aj1_gallery2.jpg');

-- 4. 쿠폰 정보 (Coupon)
INSERT INTO coupon (name, discount_type, discount_value, expiry_date)
VALUES ('신규 가입 환영 쿠폰', 'FIXED', 5000, '2026-12-31 23:59:59');

INSERT INTO coupon (name, discount_type, discount_value, expiry_date)
VALUES ('나이키 브랜드 세일 쿠폰', 'PERCENT', 10, '2026-12-31 23:59:59');

-- 5. 유저 쿠폰 매핑 (UserCoupon)
INSERT INTO user_coupon (user_id, coupon_id, status) VALUES (1, 1, 'ACTIVE');
INSERT INTO user_coupon (user_id, coupon_id, status) VALUES (1, 2, 'ACTIVE');

-- 6. 장바구니 정보 (CartItem)
INSERT INTO cart_item (user_id, product_id, quantity, selected_size) VALUES (1, 1, 1, '270');
INSERT INTO cart_item (user_id, product_id, quantity, selected_size) VALUES (1, 3, 1, 'FREE');
