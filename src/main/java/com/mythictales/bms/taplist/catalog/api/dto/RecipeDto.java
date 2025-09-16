package com.mythictales.bms.taplist.catalog.api.dto;

import java.time.Instant;
import java.util.List;

public record RecipeDto(
    Long id,
    Long breweryId,
    String name,
    String styleName,
    String type,
    Double batchSizeLiters,
    Integer boilTimeMinutes,
    Double ibu,
    Double abv,
    Double og,
    Double fg,
    Double efficiency,
    String equipment,
    String sourceFormat,
    String sourceHash,
    String notes,
    Instant createdAt,
    List<RecipeFermentableDto> fermentables,
    List<RecipeHopDto> hops,
    List<RecipeYeastDto> yeasts,
    List<RecipeMiscDto> miscs,
    List<MashStepDto> mashSteps) {}
