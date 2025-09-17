package com.mythictales.bms.taplist.keginventory.domain;

import java.time.Instant;

import com.mythictales.bms.taplist.domain.Keg;
import com.mythictales.bms.taplist.domain.Venue;

import jakarta.persistence.*;

@Entity
public class KegMovementHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  private Keg keg;

  @ManyToOne private Venue fromVenue;

  @ManyToOne private Venue toVenue;

  private String externalPartner;

  private Long actorUserId;

  @Column(nullable = false)
  private Instant movedAt = Instant.now();

  public KegMovementHistory() {}

  public KegMovementHistory(
      Keg keg, Venue fromVenue, Venue toVenue, String externalPartner, Long actorUserId) {
    this.keg = keg;
    this.fromVenue = fromVenue;
    this.toVenue = toVenue;
    this.externalPartner = externalPartner;
    this.actorUserId = actorUserId;
  }

  public Long getId() {
    return id;
  }

  public Keg getKeg() {
    return keg;
  }

  public void setKeg(Keg keg) {
    this.keg = keg;
  }

  public Venue getFromVenue() {
    return fromVenue;
  }

  public void setFromVenue(Venue fromVenue) {
    this.fromVenue = fromVenue;
  }

  public Venue getToVenue() {
    return toVenue;
  }

  public void setToVenue(Venue toVenue) {
    this.toVenue = toVenue;
  }

  public String getExternalPartner() {
    return externalPartner;
  }

  public void setExternalPartner(String externalPartner) {
    this.externalPartner = externalPartner;
  }

  public Long getActorUserId() {
    return actorUserId;
  }

  public void setActorUserId(Long actorUserId) {
    this.actorUserId = actorUserId;
  }

  public Instant getMovedAt() {
    return movedAt;
  }

  public void setMovedAt(Instant movedAt) {
    this.movedAt = movedAt;
  }
}
