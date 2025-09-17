package com.mythictales.bms.taplist.catalog.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mythictales.bms.taplist.catalog.domain.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
  Optional<Recipe> findFirstByBrewery_IdAndSourceHash(Long breweryId, String sourceHash);

  Page<Recipe> findByBrewery_Id(Long breweryId, Pageable pageable);
}
