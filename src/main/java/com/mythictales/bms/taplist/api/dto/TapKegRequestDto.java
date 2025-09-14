package com.mythictales.bms.taplist.api.dto;

import jakarta.validation.constraints.NotNull;

public record TapKegRequestDto(@NotNull Long kegId, Long actorUserId) {}

