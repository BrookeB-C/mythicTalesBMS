package com.mythictales.bms.taplist.api.dto;

public record UserDto(
    Long id, String username, String role, Long breweryId, Long barId, Long taproomId) {}
