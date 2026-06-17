package com.sofit.externalmock.domain.cb.controller;

import com.sofit.externalmock.domain.cb.dto.request.CbInquiryRequest;
import com.sofit.externalmock.domain.cb.dto.response.CbResultResponse;
import com.sofit.externalmock.domain.cb.service.CbService;
import com.sofit.externalmock.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ext/cb")
@RequiredArgsConstructor
public class CbController {

    private final CbService cbService;

    @PostMapping("/inquiry")
    public ResponseEntity<ApiResponse<CbResultResponse>> inquiry(
            @RequestBody @Valid CbInquiryRequest request
    ) {
        return ResponseEntity.ok(cbService.inquiry(request));
    }
}
