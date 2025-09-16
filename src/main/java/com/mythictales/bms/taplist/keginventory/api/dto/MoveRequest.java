package com.mythictales.bms.taplist.keginventory.api.dto;

import jakarta.validation.constraints.NotNull;

public record MoveRequest(
    @NotNull Long kegId, @NotNull Long fromVenueId, @NotNull Long toVenueId) {}
