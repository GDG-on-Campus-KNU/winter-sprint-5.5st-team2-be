-- 기존 테이블 삭제 (연관 관계 고려하여 순서대로 삭제)
DROP TABLE IF EXISTS user_coupon CASCADE;
DROP TABLE IF EXISTS coupon CASCADE;
DROP TABLE IF EXISTS order_item CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS cart_item CASCADE;
DROP TABLE IF EXISTS menu CASCADE;
DROP TABLE IF EXISTS store CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- 1. 사용자 (Users) - 비밀번호, 권한, 주소 필드 포함
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL, -- USER, ADMIN
    zip_code VARCHAR(10),
    address VARCHAR(255),
    detail_address VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. 매장 (Store)
CREATE TABLE store (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    phone VARCHAR(50)
);

-- 3. 메뉴 (Menu)
CREATE TABLE menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price INT NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    is_available BOOLEAN DEFAULT TRUE,
    category VARCHAR(50) NOT NULL, -- PIZZA, BURGER 등
    CONSTRAINT chk_menu_stock CHECK (stock >= 0),
    FOREIGN KEY (store_id) REFERENCES store(id)
);

-- 4. 장바구니 (CartItem)
CREATE TABLE cart_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    UNIQUE (user_id, menu_id), -- 중복 담기 방지
    CONSTRAINT chk_cart_quantity CHECK (quantity > 0),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (menu_id) REFERENCES menu(id)
);

-- 5. 주문 (Orders)
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(19, 2) NOT NULL,
    discount_amount DECIMAL(19, 2) DEFAULT 0,
    delivery_fee DECIMAL(19, 2) DEFAULT 0,
    final_amount DECIMAL(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL, -- PENDING, COMPLETED, CANCELLED
    address VARCHAR(255),
    coupon_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 6. 주문 상세 (OrderItem)
CREATE TABLE order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    order_price DECIMAL(19, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (menu_id) REFERENCES menu(id)
);

-- 7. 쿠폰 (Coupon)
CREATE TABLE coupon (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    discount_type VARCHAR(20) NOT NULL, -- PERCENT, FIXED
    discount_value DECIMAL(19, 2) NOT NULL,
    min_order_amount DECIMAL(19, 2) DEFAULT 0,
    expiry_date TIMESTAMP
);

-- 8. 사용자 쿠폰 (UserCoupon)
CREATE TABLE user_coupon (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    coupon_id BIGINT NOT NULL,
    is_used BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (coupon_id) REFERENCES coupon(id)
);