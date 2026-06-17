package com.sofit.externalmock.domain.kyc.controller;

import com.sofit.externalmock.domain.kyc.dto.request.KycVerifyRequest;
import com.sofit.externalmock.domain.kyc.dto.response.KycVerifyResponse;
import com.sofit.externalmock.domain.kyc.service.KycService;
import com.sofit.externalmock.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ext/kyc")
@RequiredArgsConstructor
public class KycController {

    private final KycService kycService;

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<KycVerifyResponse>> verify(
            @RequestBody @Valid KycVerifyRequest request
    ) {
        return ResponseEntity.ok(kycService.verify(request));
    }
}
