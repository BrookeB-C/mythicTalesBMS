package com.mythictales.bms.taplist.catalog.repo;

import com.mythictales.bms.taplist.catalog.domain.BjcpStyle;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BjcpStyleRepository extends JpaRepository<BjcpStyle, Long> {
  Optional<BjcpStyle> findFirstByCodeAndYear(String code, Integer year);
}

