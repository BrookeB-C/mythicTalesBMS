package com.mythictales.bms.taplist.domain;

import java.time.Instant;

import jakarta.persistence.*;

@Entity
@Table(name = "taplist_view")
public class TaplistView {
  @Id
  @Column(name = "tap_id")
  private Long tapId;

  @Column(name = "venue_id")
  private Long venueId;

  @Column(name = "beer_name")
  private String beerName;

  private String style;
  private Double abv;

  @Column(name = "remaining_ounces")
  private Double remainingOunces;

  @Column(name = "total_ounces")
  private Double totalOunces;

  @Column(name = "fill_percent")
  private Integer fillPercent;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt = Instant.now();

  public TaplistView() {}

  public TaplistView(Long tapId) {
    this.tapId = tapId;
  }

  public Long getTapId() { return tapId; }
  public void setTapId(Long tapId) { this.tapId = tapId; }
  public Long getVenueId() { return venueId; }
  public void setVenueId(Long venueId) { this.venueId = venueId; }
  public String getBeerName() { return beerName; }
  public void setBeerName(String beerName) { this.beerName = beerName; }
  public String getStyle() { return style; }
  public void setStyle(String style) { this.style = style; }
  public Double getAbv() { return abv; }
  public void setAbv(Double abv) { this.abv = abv; }
  public Double getRemainingOunces() { return remainingOunces; }
  public void setRemainingOunces(Double remainingOunces) { this.remainingOunces = remainingOunces; }
  public Double getTotalOunces() { return totalOunces; }
  public void setTotalOunces(Double totalOunces) { this.totalOunces = totalOunces; }
  public Integer getFillPercent() { return fillPercent; }
  public void setFillPercent(Integer fillPercent) { this.fillPercent = fillPercent; }
  public Instant getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}

