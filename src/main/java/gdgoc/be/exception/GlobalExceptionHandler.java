package gdgoc.be.exception;


import gdgoc.be.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    /**
     *
     * @param e
     * @return ResponseEntity
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException e) {

        log.error("BusinessException: {}", e.getMessage());

        BusinessErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(errorCode.getMessage()));
    }


    /**
     * BusinessException 외 모든 시스템 예외를 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {

        log.error("Unhandled Exception: ", e);

        return ResponseEntity
                .status(500)
                .body(ApiResponse.fail("서버 내부 오류가 발생했습니다."));
    }
}
