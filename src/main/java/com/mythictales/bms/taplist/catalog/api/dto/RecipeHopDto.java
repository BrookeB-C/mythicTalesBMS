package com.mythictales.bms.taplist.catalog.api.dto;

public record RecipeHopDto(
    Long id,
    String name,
    Double alphaAcid,
    Double amountGrams,
    Integer timeMinutes,
    String useFor,
    String form,
    Double ibuContribution) {}
