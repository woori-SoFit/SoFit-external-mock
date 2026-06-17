package com.sofit.externalmock.domain.cb.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CbInquiryRequest(
        @NotBlank(message = "이름은 필수입니다.") String name,
        @NotBlank(message = "주민번호는 필수입니다.") String residentNumber
) {
}
