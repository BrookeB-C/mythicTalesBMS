package com.mythictales.bms.taplist.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mythictales.bms.taplist.domain.Venue;
import com.mythictales.bms.taplist.domain.VenueType;

public interface VenueRepository extends JpaRepository<Venue, Long> {
  List<Venue> findByBreweryId(Long breweryId);

  Optional<Venue> findFirstByBreweryIdAndType(Long breweryId, VenueType type);

  Optional<Venue> findFirstByName(String name);
}
