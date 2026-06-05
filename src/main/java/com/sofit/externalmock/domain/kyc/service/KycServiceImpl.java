package com.sofit.externalmock.domain.kyc.service;

import com.sofit.externalmock.domain.kyc.dto.request.KycVerifyRequest;
import com.sofit.externalmock.domain.kyc.dto.response.KycVerifyResponse;
import com.sofit.externalmock.domain.kyc.entity.ExtKycRecord;
import com.sofit.externalmock.domain.kyc.exception.KycErrorCode;
import com.sofit.externalmock.domain.kyc.repository.ExtKycRecordRepository;
import com.sofit.externalmock.global.apiPayload.ApiResponse;
import com.sofit.externalmock.global.apiPayload.BaseException;
import com.sofit.externalmock.global.apiPayload.code.GeneralSuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KycServiceImpl implements KycService {

    private final ExtKycRecordRepository extKycRecordRepository;

    @Override
    public ApiResponse<KycVerifyResponse> verify(KycVerifyRequest request) {
        ExtKycRecord record = extKycRecordRepository.findByBusinessNumber(request.businessNumber())
                .orElseThrow(() -> new BaseException(KycErrorCode.KYC_NOT_FOUND));

        KycVerifyResponse response = KycVerifyResponse.from(record);
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, response);
    }
}
