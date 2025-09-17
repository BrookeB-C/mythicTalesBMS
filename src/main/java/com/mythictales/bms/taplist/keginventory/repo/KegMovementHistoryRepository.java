package com.mythictales.bms.taplist.keginventory.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mythictales.bms.taplist.keginventory.domain.KegMovementHistory;

public interface KegMovementHistoryRepository extends JpaRepository<KegMovementHistory, Long> {
  Page<KegMovementHistory> findByKeg_Id(Long kegId, Pageable pageable);

  Page<KegMovementHistory> findByToVenue_Id(Long toVenueId, Pageable pageable);
}
