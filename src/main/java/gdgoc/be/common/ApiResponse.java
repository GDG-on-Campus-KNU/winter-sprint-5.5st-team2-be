package gdgoc.be.common;

import lombok.Getter;

@Getter
public class ApiResponse<T> {

    private final boolean success;
    private final T data;
    private final String message;

    public ApiResponse(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    // 성공 시 반환되는 ApiResponse
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true,data,null);
    }
}
