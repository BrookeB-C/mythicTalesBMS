package com.mythictales.bms.taplist.api.dto;

public record KegDto(
    Long id,
    BeerDto beer,
    Long breweryId,
    KegSizeSpecDto size,
    double totalOunces,
    double remainingOunces,
    String status,
    Long assignedVenueId,
    String serialNumber,
    Long version) {}
