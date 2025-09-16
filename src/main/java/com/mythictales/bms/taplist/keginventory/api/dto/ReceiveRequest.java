package com.mythictales.bms.taplist.keginventory.api.dto;

import jakarta.validation.constraints.NotNull;

public record ReceiveRequest(@NotNull Long kegId, @NotNull Long venueId) {}
