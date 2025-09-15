package com.mythictales.bms.taplist.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mythictales.bms.taplist.domain.Taproom;

public interface TaproomRepository extends JpaRepository<Taproom, Long> {
  List<Taproom> findByBreweryId(Long breweryId);

  Page<Taproom> findByBreweryId(Long breweryId, Pageable pageable);
}
