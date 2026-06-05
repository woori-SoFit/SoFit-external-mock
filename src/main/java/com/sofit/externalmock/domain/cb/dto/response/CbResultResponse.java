package com.sofit.externalmock.domain.cb.dto.response;

import com.sofit.externalmock.domain.cb.entity.ExtCbResult;

import java.time.LocalDateTime;

public record CbResultResponse(
        String name,
        Integer creditScore,
        LocalDateTime evaluatedAt
) {
    public static CbResultResponse from(ExtCbResult result) {
        return new CbResultResponse(
                result.getName(),
                result.getCreditScore(),
                result.getEvaluatedAt()
        );
    }
}
