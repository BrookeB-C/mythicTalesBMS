package com.mythictales.bms.taplist.catalog.api.dto;

public record RecipeMiscDto(
    Long id, String name, String type, Double amount, String amountUnit, String useFor) {}
