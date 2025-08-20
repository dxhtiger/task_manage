package org.example.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "操作成功", defaultIfNull(data));
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, defaultIfNull(data));
    }

    public static <T> ApiResponse<T> fail(String message) {
        // 这里也可以返回空字符串或空对象，避免 data = null
        return new ApiResponse<>(500, message, defaultIfNull(null));
    }

    /**
     * 如果传入 data 为 null，返回默认值，避免接口响应结构中出现 null
     */
    @SuppressWarnings("unchecked")
    private static <T> T defaultIfNull(T data) {
        if (data == null) {
            return (T) ""; // 也可以返回 new HashMap<>() 或自定义空对象
        }
        return data;
    }
}
