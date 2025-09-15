package com.mythictales.bms.taplist.api.dto;

import java.time.Instant;

public record KegEventDto(
    Long id,
    Long venueId,
    Long tapId,
    Long kegId,
    String type,
    Double ounces,
    Long actorUserId,
    Instant createdAt) {}
