package com.mythictales.bms.taplist.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record BlowRequestDto(
    @Schema(description = "Actor user id (optional)", example = "42") Long actorUserId,
    @Schema(
            description = "Expected current version of the Tap for optimistic concurrency",
            example = "5")
        Long expectedVersion) {}
