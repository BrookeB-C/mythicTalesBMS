package com.mythictales.bms.taplist.catalog.api.dto;

public record MashStepDto(
    Long id,
    String name,
    String type,
    Double stepTempC,
    Integer stepTimeMinutes,
    Double infuseAmountLiters) {}
