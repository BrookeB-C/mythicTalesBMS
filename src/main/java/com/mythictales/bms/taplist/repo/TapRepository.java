package com.mythictales.bms.taplist.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mythictales.bms.taplist.domain.Tap;

public interface TapRepository extends JpaRepository<Tap, Long> {
  List<Tap> findByTaproomId(Long taproomId);

  List<Tap> findByBarId(Long barId);

  List<Tap> findByVenueId(Long venueId);

  List<Tap> findByTaproomIdAndKegIsNull(Long taproomId);

  List<Tap> findByBarIdAndKegIsNull(Long barId);

  List<Tap> findByVenueIdAndKegIsNull(Long venueId);
}
