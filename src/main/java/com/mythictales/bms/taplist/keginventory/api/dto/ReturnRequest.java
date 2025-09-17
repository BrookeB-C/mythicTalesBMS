package com.mythictales.bms.taplist.keginventory.api.dto;

import jakarta.validation.constraints.NotNull;

public record ReturnRequest(@NotNull Long kegId) {}
