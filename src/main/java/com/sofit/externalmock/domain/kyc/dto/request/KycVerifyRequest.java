package com.sofit.externalmock.domain.kyc.dto.request;

import jakarta.validation.constraints.NotBlank;

public record KycVerifyRequest(
        @NotBlank(message = "사업자등록번호는 필수입니다.") String businessNumber
) {
}
