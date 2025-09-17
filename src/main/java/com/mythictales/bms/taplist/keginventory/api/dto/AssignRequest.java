package com.mythictales.bms.taplist.keginventory.api.dto;

import jakarta.validation.constraints.NotNull;

public record AssignRequest(@NotNull Long kegId, @NotNull Long venueId) {}
