package com.sofit.externalmock.domain.cb.exception;

import com.sofit.externalmock.global.apiPayload.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CbErrorCode implements ErrorCode {
    CB_NOT_FOUND(404, "CB4001", "등록된 CB 정보를 찾을 수 없습니다.");

    private final int status;
    private final String code;
    private final String message;
}
