package com.sofit.externalmock.domain.cb.service;

import com.sofit.externalmock.domain.cb.dto.request.CbInquiryRequest;
import com.sofit.externalmock.domain.cb.dto.response.CbResultResponse;
import com.sofit.externalmock.domain.cb.entity.ExtCbResult;
import com.sofit.externalmock.domain.cb.exception.CbErrorCode;
import com.sofit.externalmock.domain.cb.repository.ExtCbResultRepository;
import com.sofit.externalmock.global.apiPayload.ApiResponse;
import com.sofit.externalmock.global.apiPayload.BaseException;
import com.sofit.externalmock.global.apiPayload.code.GeneralSuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CbServiceImpl implements CbService {

    private final ExtCbResultRepository extCbResultRepository;

    @Override
    public ApiResponse<CbResultResponse> inquiry(CbInquiryRequest request) {
        ExtCbResult result = extCbResultRepository.findByNameAndResidentNumber(
                        request.name(), request.residentNumber())
                .orElseThrow(() -> new BaseException(CbErrorCode.CB_NOT_FOUND));

        return ApiResponse.onSuccess(GeneralSuccessCode.OK, CbResultResponse.from(result));
    }
}
