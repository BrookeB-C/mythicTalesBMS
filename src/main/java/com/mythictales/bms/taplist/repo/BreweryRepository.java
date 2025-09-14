package com.mythictales.bms.taplist.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mythictales.bms.taplist.domain.Brewery;

public interface BreweryRepository extends JpaRepository<Brewery, Long> {}
