package com.mythictales.bms.taplist.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mythictales.bms.taplist.domain.TaplistView;

public interface TaplistViewRepository extends JpaRepository<TaplistView, Long> {
  List<TaplistView> findByVenueIdOrderByTapIdAsc(Long venueId);
}

