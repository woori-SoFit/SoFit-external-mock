package com.sofit.externalmock.domain.financialcert.service;

import com.sofit.externalmock.domain.financialcert.dto.request.FinancialCertVerifyRequest;
import com.sofit.externalmock.domain.financialcert.dto.response.FinancialCertVerifyResponse;
import com.sofit.externalmock.global.apiPayload.BaseResponse;

public interface FinancialCertService {
    BaseResponse<FinancialCertVerifyResponse> verify(FinancialCertVerifyRequest request);
}
