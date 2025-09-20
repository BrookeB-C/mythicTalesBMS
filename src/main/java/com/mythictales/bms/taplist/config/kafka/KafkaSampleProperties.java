package com.mythictales.bms.taplist.config.kafka;

import java.util.Optional;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bms.kafka.sample")
public class KafkaSampleProperties {

  private boolean enabled = false;
  private String breweryId = "00000000-0000-0000-0000-000000000001";
  private String facilityId = "00000000-0000-0000-0000-000000000010";
  private String venueId;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getBreweryId() {
    return breweryId;
  }

  public void setBreweryId(String breweryId) {
    this.breweryId = breweryId == null ? null : breweryId.trim();
  }

  public Optional<String> getFacilityId() {
    return Optional.ofNullable(facilityId).map(String::trim).filter(value -> !value.isBlank());
  }

  public void setFacilityId(String facilityId) {
    this.facilityId = facilityId;
  }

  public Optional<String> getVenueId() {
    return Optional.ofNullable(venueId);
  }

  public void setVenueId(String venueId) {
    this.venueId = venueId;
  }
}
