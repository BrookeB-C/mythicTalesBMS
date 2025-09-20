package com.mythictales.bms.taplist.keginventory.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mythictales.bms.taplist.keginventory.domain.KegMovementHistory;

public interface KegMovementHistoryRepository extends JpaRepository<KegMovementHistory, Long> {
  Page<KegMovementHistory> findByKeg_Id(Long kegId, Pageable pageable);

  Page<KegMovementHistory> findByToVenue_Id(Long toVenueId, Pageable pageable);

  Page<KegMovementHistory> findByMovedAtBetween(
      java.time.Instant from, java.time.Instant to, Pageable pageable);

  Page<KegMovementHistory> findByKeg_IdAndMovedAtBetween(
      Long kegId, java.time.Instant from, java.time.Instant to, Pageable pageable);

  Page<KegMovementHistory> findByToVenue_IdAndMovedAtBetween(
      Long toVenueId, java.time.Instant from, java.time.Instant to, Pageable pageable);
}
