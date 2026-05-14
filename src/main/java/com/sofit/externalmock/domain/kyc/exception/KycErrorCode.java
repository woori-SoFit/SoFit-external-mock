package com.sofit.externalmock.domain.kyc.exception;

import com.sofit.externalmock.global.apiPayload.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KycErrorCode implements ErrorCode {
    KYC_NOT_FOUND(404, "KYC4001", "등록된 사업자 정보를 찾을 수 없습니다.");

    private final int status;
    private final String code;
    private final String message;
}
