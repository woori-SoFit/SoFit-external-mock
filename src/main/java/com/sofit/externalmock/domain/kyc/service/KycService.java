package com.sofit.externalmock.domain.kyc.service;

import com.sofit.externalmock.domain.kyc.dto.request.KycVerifyRequest;
import com.sofit.externalmock.domain.kyc.dto.response.KycVerifyResponse;
import com.sofit.externalmock.global.apiPayload.ApiResponse;

public interface KycService {
    ApiResponse<KycVerifyResponse> verify(KycVerifyRequest request);
}
