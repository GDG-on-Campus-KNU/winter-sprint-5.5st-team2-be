-- 1. 사용자 정보 (User)
-- 비밀번호는 'password1~5' BCrypt 인코딩 값
INSERT INTO users (name, email, password, phone, role, zip_code, address, detail_address) VALUES
('홍길동', 'user1@example.com', '$2a$10$lG.hGrWA8KvC2O5/G3tNE.gD7ziSDG9V8BkMjzznYts2Smfzbxzei', '010-1234-5678', 'USER', '12345', '서울시 강남구', '테헤란로 123'),
('김철수', 'user2@example.com', '$2a$10$hfBBju4c97a2vkXcpS/0pu3hCuzkQ.alpKieaKfQAC1ySD3tbDiZG', '010-9876-5432', 'USER', '54321', '부산시 해운대구', '해운대해변로 456'),
('이영희', 'user3@example.com', '$2a$10$s4M3oA70clPfG2beCjBz0eYWVSmYt/DMM6hlP1aVrZGu0yuLqJG3G', '010-1111-2222', 'USER', '11111', '대구시 중구', '동성로 1길'),
('박지민', 'user4@example.com', '$2a$10$9kkaixjSgTeXqvE6cbW6Y.ZoQHW4rmbG6H4JZMi8ntLf.bRNdG38m', '010-3333-4444', 'USER', '22222', '광주시 서구', '상무대로 789'),
('관리자', 'admin@example.com', '$2a$10$oD/fW82L8KjXQHXv8Z6XxeKBXrozCReNtYQe7RJIrRx5jy3zXBhqC', '010-0000-0000', 'ADMIN', '00000', '서울시 본사', '관리실');

-- 2. 상점 정보 (Store)
INSERT INTO store (name, address, phone) VALUES
('강남점', '서울시 강남구', '02-123-4567'),
('온라인몰', '서울특별시 본사', '080-123-4567'),
('대구동성로점', '대구시 중구', '053-123-4567');

-- 3. 상품 정보 (Product) - 카테고리별 대량 추가 (OUTER, TOP, BOTTOM, BAG, SHOES, ACCESSORY)
INSERT INTO products (store_id, brand, name, image_url, original_price, discount_rate, price, stock, is_available, description, rating, category) VALUES
-- [SHOES 카테고리]
(2, 'Nike', 'Air Jordan 1 Retro High OG', 'https://images.example.com/nike/aj1.jpg', 200000, 10, 180000, 50, TRUE, 'The classic AJ1 in High OG silhouette.', 4.8, 'SHOES'),
(2, 'Adidas', 'Yeezy Boost 350 V2', 'https://images.example.com/adidas/yeezy350.jpg', 300000, 0, 300000, 20, TRUE, 'Iconic Yeezy Boost 350 V2 in Beluga colorway.', 4.9, 'SHOES'),
(2, 'New Balance', '990v6 Made in USA', 'https://images.example.com/nb/990v6.jpg', 280000, 5, 266000, 30, TRUE, 'Premium cushioning and classic design.', 4.7, 'SHOES'),
(2, 'Converse', 'Chuck Taylor All Star', 'https://images.example.com/converse/chuck.jpg', 65000, 0, 65000, 100, TRUE, 'The most iconic sneaker in the world.', 4.5, 'SHOES'),
(2, 'Vans', 'Old Skool Classic', 'https://images.example.com/vans/oldskool.jpg', 75000, 10, 67500, 120, TRUE, 'Skate classic with the iconic side stripe.', 4.6, 'SHOES'),
(2, 'Crocs', 'Classic Clog', 'https://images.example.com/crocs/classic.jpg', 55000, 20, 44000, 200, TRUE, 'Comfortable slip-on clogs.', 4.4, 'SHOES'),

-- [TOP 카테고리]
(2, 'Polo Ralph Lauren', 'Oxford Cotton Shirt', 'https://images.example.com/polo/oxford.jpg', 150000, 10, 135000, 80, TRUE, 'Classic fit oxford shirt.', 4.8, 'TOP'),
(2, 'Patagonia', 'P-6 Logo Pocket T-Shirt', 'https://images.example.com/patagonia/p6.jpg', 69000, 0, 69000, 150, TRUE, '100% recycled cotton tee.', 4.7, 'TOP'),
(2, 'Stussy', 'Basic Stussy Tee', 'https://images.example.com/stussy/basic.jpg', 58000, 0, 58000, 40, TRUE, 'Classic stussy logo short sleeve.', 4.9, 'TOP'),
(2, 'Nike', 'Sportswear Club Fleece Hoodie', 'https://images.example.com/nike/hoodie.jpg', 79000, 15, 67150, 100, TRUE, 'Everyday comfort fleece hoodie.', 4.8, 'TOP'),
(2, 'Carhartt WIP', 'Chase Sweatshirt', 'https://images.example.com/carhartt/chase.jpg', 108000, 5, 102600, 60, TRUE, 'Heavyweight cotton blend sweater.', 4.6, 'TOP'),
(2, 'Uniqlo', 'Supima Cotton Crew Neck T', 'https://images.example.com/uniqlo/supima.jpg', 19900, 0, 19900, 300, TRUE, 'Premium quality daily tee.', 4.5, 'TOP'),
(2, 'Ami', 'Ami de Coeur Sweatshirt', 'https://images.example.com/ami/sweat.jpg', 350000, 10, 315000, 30, TRUE, 'Signature heart logo sweatshirt.', 4.9, 'TOP'),
(2, 'Maison Kitsune', 'Fox Head Patch Tee', 'https://images.example.com/kitsune/foxtee.jpg', 120000, 0, 120000, 45, TRUE, 'Cotton tee with fox patch.', 4.7, 'TOP'),
(2, 'Covernat', 'C Logo Hoodie', 'https://images.example.com/covernat/hoodie.jpg', 69000, 20, 55200, 150, TRUE, 'Casual logo hoodie.', 4.4, 'TOP'),
(2, 'Thisisneverthat', 'T-Logo Tee', 'https://images.example.com/this/tee.jpg', 49000, 10, 44100, 200, TRUE, 'Streetwear essential tee.', 4.6, 'TOP'),

-- [BOTTOM 카테고리]
(2, 'Levi''s', '501 Original Fit Jeans', 'https://images.example.com/levis/501.jpg', 129000, 15, 109650, 100, TRUE, 'The blueprint for all blue jeans.', 4.8, 'BOTTOM'),
(2, 'Musinsa Standard', 'Slacks Standard Fit', 'https://images.example.com/musinsa/slacks.jpg', 39900, 0, 39900, 400, TRUE, 'Perfect daily slacks.', 4.6, 'BOTTOM'),
(2, 'Nike', 'Sportswear Club Fleece Joggers', 'https://images.example.com/nike/jogger.jpg', 75000, 10, 67500, 120, TRUE, 'Comfortable everyday joggers.', 4.7, 'BOTTOM'),
(2, 'Dickies', '874 Work Pants', 'https://images.example.com/dickies/874.jpg', 65000, 5, 61750, 90, TRUE, 'Durable classic work pants.', 4.5, 'BOTTOM'),
(2, 'Gramicci', 'G-Pants', 'https://images.example.com/gramicci/gpants.jpg', 109000, 10, 98100, 60, TRUE, 'Outdoor climbing pants.', 4.8, 'BOTTOM'),
(2, 'Calvin Klein', 'Straight Fit Denim', 'https://images.example.com/ck/denim.jpg', 159000, 20, 127200, 50, TRUE, 'Modern straight denim.', 4.7, 'BOTTOM'),
(2, 'Adidas', 'Firebird Track Pants', 'https://images.example.com/adidas/firebird.jpg', 89000, 10, 80100, 80, TRUE, 'Classic track pants with 3 stripes.', 4.6, 'BOTTOM'),

-- [OUTER 카테고리]
(2, 'The North Face', 'Nuptse 1996 Down Jacket', 'https://images.example.com/tnf/nuptse.jpg', 350000, 0, 350000, 40, TRUE, 'Iconic puffer jacket.', 4.9, 'OUTER'),
(2, 'Patagonia', 'Classic Retro-X Fleece', 'https://images.example.com/patagonia/retrox.jpg', 289000, 5, 274550, 35, TRUE, 'Windproof fleece jacket.', 4.8, 'OUTER'),
(2, 'Barbour', 'Ashby Wax Jacket', 'https://images.example.com/barbour/ashby.jpg', 450000, 15, 382500, 20, TRUE, 'Classic British wax jacket.', 4.7, 'OUTER'),
(2, 'Alpha Industries', 'MA-1 Bomber Jacket', 'https://images.example.com/alpha/ma1.jpg', 220000, 10, 198000, 50, TRUE, 'Original flight jacket.', 4.6, 'OUTER'),
(2, 'Arc''teryx', 'Beta LT Jacket', 'https://images.example.com/arcteryx/beta.jpg', 550000, 0, 550000, 15, TRUE, 'Lightweight Gore-Tex shell.', 4.9, 'OUTER'),
(2, 'ZARA', 'Faux Leather Jacket', 'https://images.example.com/zara/leather.jpg', 129000, 20, 103200, 80, TRUE, 'Stylish biker jacket.', 4.3, 'OUTER'),
(2, 'Polo Ralph Lauren', 'Bayport Cotton Windbreaker', 'https://images.example.com/polo/wind.jpg', 250000, 10, 225000, 40, TRUE, 'Classic lightweight jacket.', 4.7, 'OUTER'),
(2, 'Covernat', 'Fleece Zip-up', 'https://images.example.com/covernat/fleece.jpg', 89000, 30, 62300, 100, TRUE, 'Cozy daily fleece.', 4.4, 'OUTER'),

-- [BAG 카테고리]
(2, 'Freitag', 'Miami Vice Tote Bag', 'https://images.example.com/freitag/miami.jpg', 180000, 0, 180000, 25, TRUE, 'Recycled tarp tote bag.', 4.8, 'BAG'),
(2, 'Arc''teryx', 'Mantis 2 Waistpack', 'https://images.example.com/arcteryx/mantis.jpg', 75000, 0, 75000, 60, TRUE, 'Versatile daily waistpack.', 4.9, 'BAG'),
(2, 'Porter', 'Tanker 2Way Briefcase', 'https://images.example.com/porter/tanker.jpg', 420000, 5, 399000, 15, TRUE, 'Classic nylon briefcase.', 4.8, 'BAG'),
(2, 'Jansport', 'SuperBreak Backpack', 'https://images.example.com/jansport/super.jpg', 49000, 10, 44100, 150, TRUE, 'The ultimate classic backpack.', 4.6, 'BAG'),
(2, 'Cos', 'Quilted Oversized Shoulder Bag', 'https://images.example.com/cos/quilted.jpg', 115000, 0, 115000, 40, TRUE, 'Popular cloud bag.', 4.7, 'BAG'),
(2, 'North Face', 'Borealis Backpack', 'https://images.example.com/tnf/borealis.jpg', 139000, 10, 125100, 70, TRUE, 'Outdoor and daily backpack.', 4.7, 'BAG'),

-- [ACCESSORY 카테고리]
(2, 'New Era', 'NY Yankees Cap', 'https://images.example.com/newera/ny.jpg', 49000, 0, 49000, 200, TRUE, 'Classic 9FORTY ballcap.', 4.7, 'ACCESSORY'),
(2, 'Apple', 'AirPods Pro 2', 'https://images.example.com/apple/airpods.jpg', 359000, 5, 341050, 100, TRUE, 'Noise cancelling wireless earbuds.', 4.9, 'ACCESSORY'),
(2, 'Casio', 'Vintage Digital Watch', 'https://images.example.com/casio/vintage.jpg', 35000, 10, 31500, 150, TRUE, 'Retro stainless steel watch.', 4.6, 'ACCESSORY'),
(2, 'Nike', 'Everyday Cushion Socks (3 Pairs)', 'https://images.example.com/nike/socks.jpg', 19000, 0, 19000, 300, TRUE, 'Essential daily sports socks.', 4.8, 'ACCESSORY'),
(2, 'Gucci', 'GG Marmont Belt', 'https://images.example.com/gucci/belt.jpg', 650000, 0, 650000, 20, TRUE, 'Luxury leather belt.', 4.9, 'ACCESSORY'),
(2, 'Gentle Monster', 'Lilit 01 Sunglasses', 'https://images.example.com/gm/lilit.jpg', 289000, 0, 289000, 40, TRUE, 'Trendy black sunglasses.', 4.8, 'ACCESSORY');


-- 4. 상품 옵션 (사이즈) - 일부 주요 상품들에 대한 사이즈 옵션 일괄 추가
INSERT INTO product_sizes (product_id, size_option) VALUES
-- 신발류 (1~6번)
(1, '260'), (1, '270'), (1, '280'),
(2, '255'), (2, '265'), (2, '275'),
(3, '260'), (3, '265'), (3, '270'),
(4, '230'), (4, '240'), (4, '250'), (4, '260'),
(5, '250'), (5, '260'), (5, '270'),

-- 의류 TOP/BOTTOM/OUTER (7~28번)
(7, 'S'), (7, 'M'), (7, 'L'), (7, 'XL'),
(8, 'M'), (8, 'L'), (8, 'XL'),
(9, 'M'), (9, 'L'), (9, 'XL'),
(10, 'S'), (10, 'M'), (10, 'L'),
(11, '28'), (11, '30'), (11, '32'), (11, '34'),
(12, '28'), (12, '30'), (12, '32'), (12, '34'),
(24, 'M'), (24, 'L'), (24, 'XL'),
(25, 'S'), (25, 'M'), (25, 'L'),

-- 가방, 액세서리류 (FREE 사이즈 지정)
(29, 'FREE'), (30, 'FREE'), (31, 'FREE'), (32, 'FREE'),
(35, 'FREE'), (36, 'FREE'), (37, 'FREE');


-- 5. 쿠폰 정보 (Coupon)
INSERT INTO coupon (name, discount_type, discount_value, expiry_date) VALUES
('신규 가입 환영 쿠폰', 'FIXED', 5000, '2026-12-31 23:59:59'),
('봄맞이 전상품 세일 쿠폰', 'PERCENT', 10, '2026-05-31 23:59:59'),
('VVIP 전용 파격 할인', 'PERCENT', 30, '2026-12-31 23:59:59'),
('무료배송 지원 쿠폰', 'FIXED', 3000, '2026-08-31 23:59:59'),
('첫 주문 감사 쿠폰', 'PERCENT', 15, '2026-12-31 23:59:59');

-- 6. 유저 쿠폰 매핑 (UserCoupon)
INSERT INTO user_coupon (user_id, coupon_id, status) VALUES
(1, 1, 'ACTIVE'), (1, 2, 'ACTIVE'), (1, 4, 'ACTIVE'),
(2, 1, 'ACTIVE'), (2, 5, 'ACTIVE'),
(3, 1, 'ACTIVE'), (3, 3, 'ACTIVE'),
(4, 2, 'ACTIVE');

-- 7. 장바구니 정보 (CartItem) - 다양한 사용자에게 장바구니 추가
INSERT INTO cart_item (user_id, product_id, quantity, selected_size) VALUES
(1, 1, 1, '270'),
(1, 7, 2, 'L'),
(1, 35, 1, 'FREE'),
(2, 4, 1, '260'),
(2, 11, 1, '32'),
(3, 24, 1, 'L'),
(3, 30, 1, 'FREE'),
(4, 10, 1, 'M');
