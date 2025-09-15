package com.mythictales.bms.taplist.catalog.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "bjcp_style", uniqueConstraints = @UniqueConstraint(name = "uq_bjcp_code_year", columnNames = {"code", "year"}))
public class BjcpStyle {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String code; // e.g., 18B

  @Column(nullable = false)
  private String name; // e.g., American Pale Ale

  private String category;      // e.g., Pale American Ale
  private String subcategory;   // e.g., B
  private Integer year;         // BJCP guideline year, e.g., 2015, 2021

  // Optional ranges (nullable)
  private Double ogMin;
  private Double ogMax;
  private Double fgMin;
  private Double fgMax;
  private Double ibuMin;
  private Double ibuMax;
  private Double abvMin;
  private Double abvMax;
  private Double srmMin;
  private Double srmMax;

  @Column(length = 2000)
  private String notes;

  public Long getId() { return id; }
  public String getCode() { return code; }
  public void setCode(String code) { this.code = code; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getCategory() { return category; }
  public void setCategory(String category) { this.category = category; }
  public String getSubcategory() { return subcategory; }
  public void setSubcategory(String subcategory) { this.subcategory = subcategory; }
  public Integer getYear() { return year; }
  public void setYear(Integer year) { this.year = year; }
  public Double getOgMin() { return ogMin; }
  public void setOgMin(Double ogMin) { this.ogMin = ogMin; }
  public Double getOgMax() { return ogMax; }
  public void setOgMax(Double ogMax) { this.ogMax = ogMax; }
  public Double getFgMin() { return fgMin; }
  public void setFgMin(Double fgMin) { this.fgMin = fgMin; }
  public Double getFgMax() { return fgMax; }
  public void setFgMax(Double fgMax) { this.fgMax = fgMax; }
  public Double getIbuMin() { return ibuMin; }
  public void setIbuMin(Double ibuMin) { this.ibuMin = ibuMin; }
  public Double getIbuMax() { return ibuMax; }
  public void setIbuMax(Double ibuMax) { this.ibuMax = ibuMax; }
  public Double getAbvMin() { return abvMin; }
  public void setAbvMin(Double abvMin) { this.abvMin = abvMin; }
  public Double getAbvMax() { return abvMax; }
  public void setAbvMax(Double abvMax) { this.abvMax = abvMax; }
  public Double getSrmMin() { return srmMin; }
  public void setSrmMin(Double srmMin) { this.srmMin = srmMin; }
  public Double getSrmMax() { return srmMax; }
  public void setSrmMax(Double srmMax) { this.srmMax = srmMax; }
  public String getNotes() { return notes; }
  public void setNotes(String notes) { this.notes = notes; }
}

