package com.mythictales.bms.taplist.catalog.api.dto;

public record RecipeYeastDto(
    Long id,
    String name,
    String laboratory,
    String productId,
    String type,
    String form,
    Double attenuation) {}
