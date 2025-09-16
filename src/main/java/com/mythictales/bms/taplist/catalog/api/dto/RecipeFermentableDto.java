package com.mythictales.bms.taplist.catalog.api.dto;

public record RecipeFermentableDto(
    Long id,
    String name,
    Double amountKg,
    Double yieldPercent,
    Double colorLovibond,
    Boolean lateAddition,
    String type) {}
