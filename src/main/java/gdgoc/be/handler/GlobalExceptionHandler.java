package gdgoc.be.handler;

import gdgoc.be.common.api.ApiResponse;
import gdgoc.be.exception.BusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ApiResponse.fail(
                        e.getErrorCode().name(),
                        e.getMessage(),
                        null
                ));
    }
}