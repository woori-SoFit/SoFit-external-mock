package com.sofit.externalmock.domain.kyc.dto.response;

import com.sofit.externalmock.domain.kyc.entity.ExtKycRecord;

import java.time.LocalDate;

public record KycVerifyResponse(
        String businessNumber,
        String representativeName,
        String businessCategory,
        String businessType,
        String businessName,
        String businessAddress,
        LocalDate openDate,
        Boolean isValid
) {
    public static KycVerifyResponse from(ExtKycRecord record) {
        return new KycVerifyResponse(
                record.getBusinessNumber(),
                record.getRepresentativeName(),
                record.getBusinessCategory(),
                record.getBusinessType(),
                record.getBusinessName(),
                record.getBusinessAddress(),
                record.getOpenDate(),
                record.getIsValid()
        );
    }
}
