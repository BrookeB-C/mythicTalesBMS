package com.mythictales.bms.taplist.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

@Entity
@Table(
    uniqueConstraints =
        @UniqueConstraint(
            name = "uq_venue_number",
            columnNames = {"venue_id", "number"}))
public class Tap {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Positive private int number;
  @ManyToOne private Keg keg;
  @ManyToOne private Venue venue;
  @Version private long version;
  @ManyToOne private Taproom taproom;
  @ManyToOne private Bar bar;

  public Tap() {}

  public Tap(int number) {
    this.number = number;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public Keg getKeg() {
    return keg;
  }

  public void setKeg(Keg keg) {
    this.keg = keg;
  }

  public Venue getVenue() {
    return venue;
  }

  public void setVenue(Venue venue) {
    this.venue = venue;
  }

  public Taproom getTaproom() {
    return taproom;
  }

  public void setTaproom(Taproom taproom) {
    this.taproom = taproom;
  }

  public Bar getBar() {
    return bar;
  }

  public void setBar(Bar bar) {
    this.bar = bar;
  }

  public long getVersion() {
    return version;
  }
}
