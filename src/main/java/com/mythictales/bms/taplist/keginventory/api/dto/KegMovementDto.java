package com.mythictales.bms.taplist.keginventory.api.dto;

import java.time.Instant;

public record KegMovementDto(
    Long id,
    Long kegId,
    Long fromVenueId,
    Long toVenueId,
    String externalPartner,
    Long actorUserId,
    Instant movedAt) {}
