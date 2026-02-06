package gdgoc.be.exception;

import lombok.Getter;

/*
    BusinessErrorCode 를 필드로 가지는 BusinessException
 */
@Getter
public class BusinessException extends RuntimeException{

    private final BusinessErrorCode errorCode;

    public BusinessException(BusinessErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
