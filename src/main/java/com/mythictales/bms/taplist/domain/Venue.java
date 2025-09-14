package com.mythictales.bms.taplist.domain;

import jakarta.persistence.*;

@Entity
public class Venue {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private VenueType type;

  @ManyToOne(optional = true)
  private Brewery brewery; // nullable for standalone bars

  public Venue() {}

  public Venue(String name, VenueType type, Brewery brewery) {
    this.name = name;
    this.type = type;
    this.brewery = brewery;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public VenueType getType() {
    return type;
  }

  public void setType(VenueType type) {
    this.type = type;
  }

  public Brewery getBrewery() {
    return brewery;
  }

  public void setBrewery(Brewery brewery) {
    this.brewery = brewery;
  }

  @Override
  public String toString() {
    return name + " (" + type + ")";
  }
}
