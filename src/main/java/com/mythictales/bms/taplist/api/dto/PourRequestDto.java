package com.mythictales.bms.taplist.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PourRequestDto(
    @NotNull @Positive Double ounces,
    Long actorUserId,
    boolean allowOverpourToBlow,
    Long expectedVersion) {}
