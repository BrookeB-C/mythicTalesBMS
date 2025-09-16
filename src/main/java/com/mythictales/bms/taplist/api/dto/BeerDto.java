package com.mythictales.bms.taplist.api.dto;

public record BeerDto(
    Long id,
    String name,
    String style,
    Long styleRefId,
    double abv,
    Long breweryId,
    String breweryName) {}
