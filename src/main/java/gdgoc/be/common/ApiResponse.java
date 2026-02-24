package gdgoc.be.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {

    private final boolean success;
    private final T data;
    private final String error;



    // 성공 시 반환되는 ApiResponse
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true,data,null);
    }
}
