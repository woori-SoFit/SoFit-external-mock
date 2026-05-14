package com.sofit.externalmock.domain.cb.repository;

import com.sofit.externalmock.domain.cb.entity.ExtCbResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExtCbResultRepository extends JpaRepository<ExtCbResult, Long> {
    Optional<ExtCbResult> findByNameAndResidentNumber(String name, String residentNumber);
}
