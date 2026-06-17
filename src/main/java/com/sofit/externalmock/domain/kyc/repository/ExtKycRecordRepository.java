package com.sofit.externalmock.domain.kyc.repository;

import com.sofit.externalmock.domain.kyc.entity.ExtKycRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExtKycRecordRepository extends JpaRepository<ExtKycRecord, Long> {
    Optional<ExtKycRecord> findByBusinessNumber(String businessNumber);
}
