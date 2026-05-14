package com.sofit.externalmock.domain.financialcert.service;

import com.sofit.externalmock.domain.financialcert.dto.request.FinancialCertVerifyRequest;
import com.sofit.externalmock.domain.financialcert.dto.response.FinancialCertVerifyResponse;
import com.sofit.externalmock.domain.financialcert.entity.ExtFinancialCert;
import com.sofit.externalmock.domain.financialcert.enums.CertStatus;
import com.sofit.externalmock.domain.financialcert.exception.FinancialCertErrorCode;
import com.sofit.externalmock.domain.financialcert.repository.ExtFinancialCertRepository;
import com.sofit.externalmock.global.apiPayload.BaseException;
import com.sofit.externalmock.global.apiPayload.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FinancialCertServiceImpl implements FinancialCertService {

    private final ExtFinancialCertRepository extFinancialCertRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public BaseResponse<FinancialCertVerifyResponse> verify(FinancialCertVerifyRequest request) {
        ExtFinancialCert cert = extFinancialCertRepository.findByPhoneNumber(request.phoneNumber())
                .orElseThrow(() -> new BaseException(FinancialCertErrorCode.CERT_NOT_FOUND));

        // 인증서 상태 검증
        if (cert.getStatus() != CertStatus.VALID) {
            FinancialCertVerifyResponse response = FinancialCertVerifyResponse.from(cert);
            return new BaseResponse<>(false, "AUTH4005", "인증서가 유효하지 않습니다.", response);
        }

        // PIN 검증
        if (!passwordEncoder.matches(request.pin(), cert.getPinHash())) {
            throw new BaseException(FinancialCertErrorCode.INVALID_PIN);
        }

        // 성공
        FinancialCertVerifyResponse response = FinancialCertVerifyResponse.from(cert);
        return BaseResponse.of("AUTH2006", "금융인증서 본인인증에 성공했습니다.", response);
    }
}
