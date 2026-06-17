package com.sofit.externalmock.domain.financialcert.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FinancialCertIdentityVerifyRequest(
        @NotBlank(message = "전화번호는 필수입니다.") String phoneNumber,
        @NotBlank(message = "이름은 필수입니다.") String holderName,
        @NotBlank(message = "주민등록번호 앞 7자리는 필수입니다.")
        @Size(min = 7, max = 7, message = "주민등록번호는 앞 7자리여야 합니다.")
        String residentNumber,
        @NotBlank(message = "PIN은 필수입니다.") String pin
) {
}
