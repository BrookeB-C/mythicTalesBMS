package com.mythictales.bms.taplist.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateUserRequestDto(
    @NotBlank String username,
    @NotBlank String password,
    @NotBlank String role,
    Long breweryId,
    Long taproomId,
    Long barId) {}
