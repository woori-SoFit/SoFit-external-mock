package com.sofit.externalmock.domain.kyc.service;

import com.sofit.externalmock.domain.kyc.dto.request.KycVerifyRequest;
import com.sofit.externalmock.domain.kyc.dto.response.KycVerifyResponse;
import com.sofit.externalmock.domain.kyc.entity.ExtKycRecord;
import com.sofit.externalmock.domain.kyc.exception.KycErrorCode;
import com.sofit.externalmock.domain.kyc.repository.ExtKycRecordRepository;
import com.sofit.externalmock.global.apiPayload.BaseException;
import com.sofit.externalmock.global.apiPayload.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KycServiceImpl implements KycService {

    private final ExtKycRecordRepository extKycRecordRepository;

    @Override
    public BaseResponse<KycVerifyResponse> verify(KycVerifyRequest request) {
        ExtKycRecord record = extKycRecordRepository.findByBusinessNumber(request.businessNumber())
                .orElseThrow(() -> new BaseException(KycErrorCode.KYC_NOT_FOUND));

        KycVerifyResponse response = KycVerifyResponse.from(record);

        // 폐업 사업자인 경우 isSuccess: true로 반환 (예외가 아닌 정상 응답, result는 포함)
        if (!record.getIsValid()) {
            return new BaseResponse<>(true, "COMMON2000", "성공입니다.", response);
        }

        return BaseResponse.success(response);
    }
}
