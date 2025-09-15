package com.mythictales.bms.taplist.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mythictales.bms.taplist.domain.KegSizeSpec;

public interface KegSizeSpecRepository extends JpaRepository<KegSizeSpec, Long> {
  Optional<KegSizeSpec> findByCode(String code);
}
