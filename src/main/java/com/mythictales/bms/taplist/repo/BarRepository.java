package com.mythictales.bms.taplist.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mythictales.bms.taplist.domain.Bar;

public interface BarRepository extends JpaRepository<Bar, Long> {
  List<Bar> findByBreweryId(Long breweryId);
}
