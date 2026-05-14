package com.sofit.externalmock.domain.financialcert.dto.request;

import jakarta.validation.constraints.NotBlank;

public record FinancialCertVerifyRequest(
        @NotBlank(message = "전화번호는 필수입니다.") String phoneNumber,
        @NotBlank(message = "PIN은 필수입니다.") String pin
) {
}
