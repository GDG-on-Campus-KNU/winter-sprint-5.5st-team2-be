package gdgoc.be.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BusinessErrorCode {

    // 요구사항에 명시된 에러 코드들
    OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "재고가 부족합니다."),
    INVALID_CART(HttpStatus.NOT_FOUND, "잘못된 장바구니 정보입니다."),
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 쿠폰입니다."),
    MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 메뉴를 찾을 수 없습니다."),

    COUPON_ALREADY_USED(HttpStatus.BAD_REQUEST, "이미 사용된 쿠폰입니다."),
    COUPON_EXPIRED(HttpStatus.BAD_REQUEST, "만료된 쿠폰입니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문 내역을 찾을 수 없습니다."),

    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN,"접근 권한이 없습니다."),

    AUTH_REQUIRED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    AUTH_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    AUTH_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.");

    private final HttpStatus status;
    private final String message;

    BusinessErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
