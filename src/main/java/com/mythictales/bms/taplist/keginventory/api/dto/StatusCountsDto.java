package com.mythictales.bms.taplist.keginventory.api.dto;

import java.util.Map;

public record StatusCountsDto(Long breweryId, Long venueId, Map<String, Long> counts) {}
