package com.mythictales.bms.taplist.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record TapKegRequestDto(
    @NotNull @Schema(description = "Keg id to place on the tap", example = "123") Long kegId,
    @Schema(description = "Actor user id (optional)", example = "42") Long actorUserId,
    @Schema(
            description = "Expected current version of the Tap for optimistic concurrency",
            example = "5")
        Long expectedVersion) {}
