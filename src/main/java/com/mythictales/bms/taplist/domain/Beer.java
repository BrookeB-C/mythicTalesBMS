package com.mythictales.bms.taplist.domain;

import com.mythictales.bms.taplist.catalog.domain.BjcpStyle;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Beer {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank private String name;
  @NotBlank private String style;

  @DecimalMin("0.0")
  @DecimalMax("20.0")
  private double abv;

  public Beer() {}

  public Beer(String n, String s, double a) {
    name = n;
    style = s;
    abv = a;
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

  public String getStyle() {
    return style;
  }

  public void setStyle(String style) {
    this.style = style;
  }

  // Optional reference to a BJCP style (normalized)
  @ManyToOne private BjcpStyle styleRef;

  public BjcpStyle getStyleRef() {
    return styleRef;
  }

  public void setStyleRef(BjcpStyle styleRef) {
    this.styleRef = styleRef;
  }

  public double getAbv() {
    return abv;
  }

  public void setAbv(double abv) {
    this.abv = abv;
  }

  // Optional reference to owning Brewery. If null, breweryName must be non-blank.
  @ManyToOne(optional = true)
  private Brewery brewery;

  // Fallback brewery name when not linked to a Brewery entity
  private String breweryName;

  public Brewery getBrewery() {
    return brewery;
  }

  public void setBrewery(Brewery brewery) {
    this.brewery = brewery;
  }

  public String getBreweryName() {
    return breweryName;
  }

  public void setBreweryName(String breweryName) {
    this.breweryName = breweryName;
  }

  @AssertTrue(message = "breweryName must be provided when breweryId is null")
  public boolean isBreweryAssociationValid() {
    if (this.brewery != null) return true;
    return this.breweryName != null && !this.breweryName.isBlank();
  }
}
