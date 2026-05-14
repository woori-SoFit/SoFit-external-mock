package com.sofit.externalmock.global.apiPayload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BaseResponse<T> {
    @JsonProperty("isSuccess")
    private final boolean success;
    private final String code;
    private final String message;
    private final T result;

    public static <T> BaseResponse<T> of(String code, String message, T result) {
        return new BaseResponse<>(true, code, message, result);
    }

    public static <T> BaseResponse<T> success(T result) {
        return new BaseResponse<>(true, "COMMON2000", "성공입니다.", result);
    }

    public static <T> BaseResponse<T> fail(String code, String message) {
        return new BaseResponse<>(false, code, message, null);
    }
}
