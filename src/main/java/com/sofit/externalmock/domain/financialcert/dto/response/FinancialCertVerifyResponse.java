package com.sofit.externalmock.domain.financialcert.dto.response;

import com.sofit.externalmock.domain.financialcert.entity.ExtFinancialCert;
import com.sofit.externalmock.domain.financialcert.enums.CertStatus;

import java.time.LocalDateTime;

public record FinancialCertVerifyResponse(
        String phoneNumber,
        String certNumber,
        String holderName,
        CertStatus status,
        LocalDateTime issuedAt,
        LocalDateTime expiresAt
) {
    public static FinancialCertVerifyResponse from(ExtFinancialCert cert) {
        return new FinancialCertVerifyResponse(
                cert.getPhoneNumber(),
                cert.getCertNumber(),
                cert.getHolderName(),
                cert.getStatus(),
                cert.getIssuedAt(),
                cert.getExpiresAt()
        );
    }
}
