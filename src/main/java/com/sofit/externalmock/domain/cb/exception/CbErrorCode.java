package com.sofit.externalmock.domain.cb.exception;

import com.sofit.externalmock.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CbErrorCode implements BaseErrorCode {
    CB_NOT_FOUND(HttpStatus.NOT_FOUND, "CB4001", "등록된 CB 정보를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
