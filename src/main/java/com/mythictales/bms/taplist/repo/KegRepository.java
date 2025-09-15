package com.mythictales.bms.taplist.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mythictales.bms.taplist.domain.Keg;
import com.mythictales.bms.taplist.domain.KegStatus;

public interface KegRepository extends JpaRepository<Keg, Long> {
  List<Keg> findByStatus(KegStatus status);

  Page<Keg> findByStatus(KegStatus status, Pageable pageable);

  List<Keg> findByBreweryId(Long breweryId);

  Page<Keg> findByBreweryId(Long breweryId, Pageable pageable);

  List<Keg> findByBreweryIdAndStatus(Long breweryId, KegStatus status);

  Page<Keg> findByBreweryIdAndStatus(Long breweryId, KegStatus status, Pageable pageable);

  List<Keg> findByAssignedVenueId(Long venueId);

  Page<Keg> findByAssignedVenueId(Long venueId, Pageable pageable);

  List<Keg> findByAssignedVenueIdAndStatus(Long venueId, KegStatus status);

  Page<Keg> findByAssignedVenueIdAndStatus(Long venueId, KegStatus status, Pageable pageable);

  List<Keg> findByBreweryIdAndAssignedVenueIsNull(Long breweryId);

  List<Keg> findByBreweryIdAndAssignedVenueIsNotNull(Long breweryId);

  List<Keg> findByBreweryIdAndAssignedVenueIsNullAndStatus(Long breweryId, KegStatus status);

  List<Keg> findByBreweryIdAndAssignedVenueIsNotNullAndStatus(Long breweryId, KegStatus status);
}
