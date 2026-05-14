package com.sofit.externalmock.domain.kyc.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ext_kyc_record")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExtKycRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long kycId;

    @Column(nullable = false, length = 20)
    private String businessNumber;

    @Column(nullable = false, length = 50)
    private String representativeName;

    @Column(length = 50)
    private String businessCategory;

    @Column(length = 50)
    private String businessType;

    @Column(length = 50)
    private String businessName;

    @Column(length = 200)
    private String businessAddress;

    private LocalDate openDate;

    @Column(nullable = false)
    private Boolean isValid;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
