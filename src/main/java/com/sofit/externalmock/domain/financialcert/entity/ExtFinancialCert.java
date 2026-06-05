package com.sofit.externalmock.domain.financialcert.entity;

import com.sofit.externalmock.domain.financialcert.enums.CertStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ext_financial_cert")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExtFinancialCert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long certId;

    @Column(nullable = false, length = 15, unique = true)
    private String phoneNumber;

    @Column(nullable = false, length = 255)
    private String pinHash;

    @Column(nullable = false, length = 100)
    private String certNumber;

    @Column(nullable = false, length = 50)
    private String holderName;

    @Column(nullable = false, length = 7)
    private String residentNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CertStatus status;

    private LocalDateTime issuedAt;

    private LocalDateTime expiresAt;
}
