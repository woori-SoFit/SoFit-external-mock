package com.sofit.externalmock.global.apiPayload;

public interface ErrorCode {
    int getStatus();
    String getCode();
    String getMessage();
}
