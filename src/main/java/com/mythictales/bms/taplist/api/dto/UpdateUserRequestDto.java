package com.mythictales.bms.taplist.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Positive;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpdateUserRequestDto(
    String password,
    String role,
    Long breweryId,
    Long taproomId,
    Long barId,
    @Positive(message = "expectedVersion must be positive") Long expectedVersion) {}
