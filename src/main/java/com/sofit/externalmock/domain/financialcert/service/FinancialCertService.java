package com.sofit.externalmock.domain.financialcert.service;

import com.sofit.externalmock.domain.financialcert.dto.request.FinancialCertIdentityVerifyRequest;
import com.sofit.externalmock.domain.financialcert.dto.request.FinancialCertLookupRequest;
import com.sofit.externalmock.domain.financialcert.dto.request.FinancialCertVerifyRequest;
import com.sofit.externalmock.domain.financialcert.dto.response.FinancialCertVerifyResponse;
import com.sofit.externalmock.global.apiPayload.ApiResponse;

public interface FinancialCertService {
    ApiResponse<FinancialCertVerifyResponse> verify(FinancialCertVerifyRequest request);

    ApiResponse<Void> identityVerify(FinancialCertIdentityVerifyRequest request);

    ApiResponse<FinancialCertVerifyResponse> lookup(FinancialCertLookupRequest request);
}
