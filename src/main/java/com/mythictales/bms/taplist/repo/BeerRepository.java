package com.mythictales.bms.taplist.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mythictales.bms.taplist.domain.Beer;

public interface BeerRepository extends JpaRepository<Beer, Long> {}
