package gdgoc.be.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BusinessErrorCode {

    // 장바구니 및 재고 관련
    OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "재고가 부족합니다."),
    INVALID_CART(HttpStatus.NOT_FOUND, "잘못된 장바구니 정보입니다."),
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 쿠폰입니다."),
    MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 메뉴를 찾을 수 없습니다."),

    // 사용자 및 주문 관련
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 주문입니다.");

    private final HttpStatus status;
    private final String message;

    BusinessErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
