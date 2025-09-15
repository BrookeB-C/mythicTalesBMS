package com.mythictales.bms.taplist.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PourRequestDto(
    @NotNull @Positive @Schema(description = "Ounces to pour", example = "12.0") Double ounces,
    @Schema(description = "Actor user id (optional)", example = "42") Long actorUserId,
    @Schema(
            description = "If true, allow overpour to blow the keg instead of returning 422",
            example = "false")
        boolean allowOverpourToBlow,
    @Schema(
            description = "Expected current version of the Tap for optimistic concurrency",
            example = "5")
        Long expectedVersion) {}
