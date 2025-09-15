package com.mythictales.bms.taplist.domain;

import jakarta.persistence.*;

@Entity
public class KegSizeSpec {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String code; // e.g., HALF_BARREL

  @Column(nullable = false)
  private double gallons;

  @Column(nullable = false)
  private double ounces;

  @Column(nullable = false)
  private double liters;

  public KegSizeSpec() {}

  public KegSizeSpec(String code, double gallons) {
    this.code = code;
    this.gallons = gallons;
    this.ounces = gallons * 128.0;
    this.liters = gallons * 3.78541;
  }

  public Long getId() {
    return id;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public double getGallons() {
    return gallons;
  }

  public void setGallons(double g) {
    this.gallons = g;
    recalc();
  }

  public double getOunces() {
    return ounces;
  }

  public double getLiters() {
    return liters;
  }

  private void recalc() {
    this.ounces = gallons * 128.0;
    this.liters = gallons * 3.78541;
  }

  @Override
  public String toString() {
    return code;
  }
}
