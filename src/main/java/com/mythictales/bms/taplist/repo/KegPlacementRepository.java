package com.mythictales.bms.taplist.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mythictales.bms.taplist.domain.KegPlacement;

public interface KegPlacementRepository extends JpaRepository<KegPlacement, Long> {
  Optional<KegPlacement> findFirstByTapIdAndEndedAtIsNull(Long tapId);

  List<KegPlacement> findByTapIdOrderByStartedAtDesc(Long tapId);
}
