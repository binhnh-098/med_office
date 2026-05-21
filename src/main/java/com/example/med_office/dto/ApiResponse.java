package com.example.med_office.dto;

import lombok.Getter;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Schema(name = "ApiResponse")
public class ApiResponse<T> {

    @Schema(example = "200")
    private final int code;

    @Schema(example = "Login successful")
    private final String message;

    private final T data;

    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(ApiCode.SUCCESS, message, data);
    }

    public static <T> ApiResponse<T> error(int code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
