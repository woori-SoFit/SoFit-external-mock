package com.sofit.externalmock.domain.kyc.service;

import com.sofit.externalmock.domain.kyc.dto.request.KycVerifyRequest;
import com.sofit.externalmock.domain.kyc.dto.response.KycVerifyResponse;
import com.sofit.externalmock.global.apiPayload.BaseResponse;

public interface KycService {
    BaseResponse<KycVerifyResponse> verify(KycVerifyRequest request);
}
