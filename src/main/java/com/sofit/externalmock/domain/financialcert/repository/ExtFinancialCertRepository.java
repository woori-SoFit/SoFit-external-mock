package com.sofit.externalmock.domain.financialcert.repository;

import com.sofit.externalmock.domain.financialcert.entity.ExtFinancialCert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExtFinancialCertRepository extends JpaRepository<ExtFinancialCert, Long> {
    Optional<ExtFinancialCert> findByPhoneNumber(String phoneNumber);
}
