package com.mythictales.bms.taplist.api.dto;

public record TapDto(
    Long id, int number, Long venueId, Long taproomId, Long barId, KegDto keg, Long version) {}
