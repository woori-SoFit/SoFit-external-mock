package com.sofit.externalmock.domain.financialcert.controller;

import com.sofit.externalmock.domain.financialcert.dto.request.FinancialCertIdentityVerifyRequest;
import com.sofit.externalmock.domain.financialcert.dto.request.FinancialCertLookupRequest;
import com.sofit.externalmock.domain.financialcert.dto.request.FinancialCertVerifyRequest;
import com.sofit.externalmock.domain.financialcert.dto.response.FinancialCertVerifyResponse;
import com.sofit.externalmock.domain.financialcert.service.FinancialCertService;
import com.sofit.externalmock.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ext/financial-certs")
@RequiredArgsConstructor
public class FinancialCertController {

    private final FinancialCertService financialCertService;

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<FinancialCertVerifyResponse>> verify(
            @RequestBody @Valid FinancialCertVerifyRequest request
    ) {
        return ResponseEntity.ok(financialCertService.verify(request));
    }

    @PostMapping("/identity-verify")
    public ResponseEntity<ApiResponse<Void>> identityVerify(
            @RequestBody @Valid FinancialCertIdentityVerifyRequest request
    ) {
        return ResponseEntity.ok(financialCertService.identityVerify(request));
    }

    @PostMapping("/lookup")
    public ResponseEntity<ApiResponse<FinancialCertVerifyResponse>> lookup(
            @RequestBody @Valid FinancialCertLookupRequest request
    ) {
        return ResponseEntity.ok(financialCertService.lookup(request));
    }
}
