-- 1. 사용자 (Users)
DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL,
    zip_code VARCHAR(10),
    address VARCHAR(255),
    detail_address VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. 상점 (Store)
DROP TABLE IF EXISTS store CASCADE;
CREATE TABLE store (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    phone VARCHAR(50)
);

-- 3. 상품 (Product)
DROP TABLE IF EXISTS products CASCADE;
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT NOT NULL,
    brand VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    original_price INT NOT NULL,
    discount_rate INT NOT NULL,
    price INT NOT NULL,
    stock INT NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    description TEXT,
    rating DOUBLE DEFAULT 0.0,
    category VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (store_id) REFERENCES store(id),
    CONSTRAINT chk_product_stock CHECK (stock >= 0)
);

-- 상품 옵션/이미지 컬렉션 테이블
DROP TABLE IF EXISTS product_sizes CASCADE;
CREATE TABLE product_sizes (
    product_id BIGINT NOT NULL,
    size_option VARCHAR(50) NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

DROP TABLE IF EXISTS product_detail_images CASCADE;
CREATE TABLE product_detail_images (
    product_id BIGINT NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

DROP TABLE IF EXISTS product_gallery_images CASCADE;
CREATE TABLE product_gallery_images (
    product_id BIGINT NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- 4. 장바구니 (CartItem)
DROP TABLE IF EXISTS cart_item CASCADE;
CREATE TABLE cart_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    selected_size VARCHAR(50) NOT NULL, -- 이 줄을 추가하세요!
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    UNIQUE (user_id, product_id, selected_size) -- 복합 유니크 제약도 수정 (사이즈별 중복 방지)
);

-- 5. 쿠폰 (Coupon)
DROP TABLE IF EXISTS coupon CASCADE;
CREATE TABLE coupon (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    discount_amount INT NOT NULL,
    min_order_amount INT NOT NULL,
    expire_date DATE NOT NULL
);

-- 6. 주문 (Order)
DROP TABLE IF EXISTS orders CASCADE;
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    coupon_id BIGINT,
    total_amount DECIMAL(19, 2) NOT NULL,
    discount_amount DECIMAL(19, 2) NOT NULL,
    final_amount DECIMAL(19, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (coupon_id) REFERENCES coupon(id)
);

-- 7. 주문 상세 (OrderItem)
DROP TABLE IF EXISTS order_item CASCADE;
CREATE TABLE order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    order_price DECIMAL(19, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- 8. 유저 쿠폰 매핑 (UserCoupon)
DROP TABLE IF EXISTS user_coupon CASCADE;
CREATE TABLE user_coupon (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    coupon_id BIGINT NOT NULL,
    is_used BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (coupon_id) REFERENCES coupon(id)
);
