package com.sofit.externalmock.domain.cb.service;

import com.sofit.externalmock.domain.cb.dto.request.CbInquiryRequest;
import com.sofit.externalmock.domain.cb.dto.response.CbResultResponse;
import com.sofit.externalmock.global.apiPayload.ApiResponse;

public interface CbService {
    ApiResponse<CbResultResponse> inquiry(CbInquiryRequest request);
}
