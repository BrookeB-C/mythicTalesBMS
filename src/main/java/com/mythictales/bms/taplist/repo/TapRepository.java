package com.mythictales.bms.taplist.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mythictales.bms.taplist.domain.Tap;

public interface TapRepository extends JpaRepository<Tap, Long> {
  List<Tap> findByTaproomId(Long taproomId);

  Page<Tap> findByTaproomId(Long taproomId, Pageable pageable);

  List<Tap> findByBarId(Long barId);

  Page<Tap> findByBarId(Long barId, Pageable pageable);

  List<Tap> findByVenueId(Long venueId);

  Page<Tap> findByVenueId(Long venueId, Pageable pageable);

  List<Tap> findByVenueBreweryId(Long breweryId);

  Page<Tap> findByVenueBreweryId(Long breweryId, Pageable pageable);

  List<Tap> findByTaproomIdAndKegIsNull(Long taproomId);

  List<Tap> findByBarIdAndKegIsNull(Long barId);

  List<Tap> findByVenueIdAndKegIsNull(Long venueId);
}
