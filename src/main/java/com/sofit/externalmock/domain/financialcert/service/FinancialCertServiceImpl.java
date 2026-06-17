package com.sofit.externalmock.domain.financialcert.service;

import com.sofit.externalmock.domain.financialcert.dto.request.FinancialCertIdentityVerifyRequest;
import com.sofit.externalmock.domain.financialcert.dto.request.FinancialCertLookupRequest;
import com.sofit.externalmock.domain.financialcert.dto.request.FinancialCertVerifyRequest;
import com.sofit.externalmock.domain.financialcert.dto.response.FinancialCertVerifyResponse;
import com.sofit.externalmock.domain.financialcert.entity.ExtFinancialCert;
import com.sofit.externalmock.domain.financialcert.enums.CertStatus;
import com.sofit.externalmock.domain.financialcert.exception.FinancialCertErrorCode;
import com.sofit.externalmock.domain.financialcert.exception.FinancialCertSuccessCode;
import com.sofit.externalmock.domain.financialcert.repository.ExtFinancialCertRepository;
import com.sofit.externalmock.global.apiPayload.ApiResponse;
import com.sofit.externalmock.global.apiPayload.BaseException;
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
    public ApiResponse<FinancialCertVerifyResponse> verify(FinancialCertVerifyRequest request) {
        ExtFinancialCert cert = extFinancialCertRepository.findByPhoneNumber(request.phoneNumber())
                .orElseThrow(() -> new BaseException(FinancialCertErrorCode.CERT_NOT_FOUND));

        // 인증서 상태 검증
        if (cert.getStatus() != CertStatus.VALID) {
            throw new BaseException(FinancialCertErrorCode.CERT_NOT_FOUND);
        }

        // PIN 검증
        if (!passwordEncoder.matches(request.pin(), cert.getPinHash())) {
            throw new BaseException(FinancialCertErrorCode.INVALID_PIN);
        }

        // 성공
        FinancialCertVerifyResponse response = FinancialCertVerifyResponse.from(cert);
        return ApiResponse.onSuccess(FinancialCertSuccessCode.PIN_VERIFIED, response);
    }

    @Override
    public ApiResponse<Void> identityVerify(FinancialCertIdentityVerifyRequest request) {
        ExtFinancialCert cert = extFinancialCertRepository.findByPhoneNumber(request.phoneNumber())
                .orElseThrow(() -> new BaseException(FinancialCertErrorCode.CERT_NOT_FOUND));

        // 인증서 상태 검증
        if (cert.getStatus() != CertStatus.VALID) {
            throw new BaseException(FinancialCertErrorCode.CERT_NOT_FOUND);
        }

        // 본인확인: 이름 + 주민등록번호 앞 7자리 매칭
        if (!cert.getHolderName().equals(request.holderName())
                || !cert.getResidentNumber().equals(request.residentNumber())) {
            throw new BaseException(FinancialCertErrorCode.IDENTITY_MISMATCH);
        }

        // PIN 검증
        if (!passwordEncoder.matches(request.pin(), cert.getPinHash())) {
            throw new BaseException(FinancialCertErrorCode.INVALID_PIN);
        }

        // 성공
        return ApiResponse.onSuccess(FinancialCertSuccessCode.IDENTITY_VERIFIED);
    }

    @Override
    public ApiResponse<FinancialCertVerifyResponse> lookup(FinancialCertLookupRequest request) {
        ExtFinancialCert cert = extFinancialCertRepository.findByPhoneNumber(request.phoneNumber())
                .orElseThrow(() -> new BaseException(FinancialCertErrorCode.CERT_NOT_FOUND));

        // 인증서 상태 검증
        if (cert.getStatus() != CertStatus.VALID) {
            throw new BaseException(FinancialCertErrorCode.CERT_NOT_FOUND);
        }

        // 본인확인: 이름 + 주민등록번호 앞 7자리 매칭
        if (!cert.getHolderName().equals(request.holderName())
                || !cert.getResidentNumber().equals(request.residentNumber())) {
            throw new BaseException(FinancialCertErrorCode.IDENTITY_MISMATCH);
        }

        // 성공 — 인증서 정보 반환
        FinancialCertVerifyResponse response = FinancialCertVerifyResponse.from(cert);
        return ApiResponse.onSuccess(FinancialCertSuccessCode.CERT_LOOKUP_SUCCESS, response);
    }
}
