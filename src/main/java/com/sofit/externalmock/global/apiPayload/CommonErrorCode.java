package com.sofit.externalmock.global.apiPayload;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
    INTERNAL_SERVER_ERROR(500, "COMMON5000", "서버 에러, 관리자에게 문의 바랍니다."),
    BAD_REQUEST(400, "COMMON4000", "잘못된 요청입니다."),
    NOT_FOUND(404, "COMMON4004", "요청한 리소스를 찾을 수 없습니다.");

    private final int status;
    private final String code;
    private final String message;
}
