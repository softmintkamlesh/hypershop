// GlobalResponse.java
package com.backend.hypershop.dto.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GlobalResponse<T> {

    private Integer status;
    private String message;
    private T data;

    // Success responses
    public static <T> GlobalResponse<T> success(T data) {
        return GlobalResponse.<T>builder()
                .status(1)
                .message("Success")
                .data(data)
                .build();
    }

    public static <T> GlobalResponse<T> success(String message, T data) {
        return GlobalResponse.<T>builder()
                .status(1)
                .message(message)
                .data(data)
                .build();
    }

    public static GlobalResponse<Object> success(String message) {
        return GlobalResponse.builder()
                .status(1)
                .message(message)
                .data(null)
                .build();
    }

    // Failure responses
    public static GlobalResponse<Object> failure(String message) {
        return GlobalResponse.builder()
                .status(0)
                .message(message)
                .data(null)
                .build();
    }

    // Custom status response
    public static <T> GlobalResponse<T> custom(int status, String message, T data) {
        return GlobalResponse.<T>builder()
                .status(status)
                .message(message)
                .data(data)
                .build();
    }
}
