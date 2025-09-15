package com.mythictales.bms.taplist.catalog.repo;

import com.mythictales.bms.taplist.catalog.domain.Recipe;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
  Optional<Recipe> findFirstByBrewery_IdAndSourceHash(Long breweryId, String sourceHash);
}

