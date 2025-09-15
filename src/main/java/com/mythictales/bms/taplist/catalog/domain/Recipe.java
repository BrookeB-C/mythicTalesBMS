package com.mythictales.bms.taplist.catalog.domain;

import jakarta.persistence.*;
import com.mythictales.bms.taplist.domain.Brewery;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recipe")
public class Recipe {
  public enum SourceFormat { BEERXML, BEERSMITH }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(name = "style_name")
  private String styleName;
  private String type; // All Grain, Extract, Partial Mash

  // Core metrics
  @Column(name = "batch_size_liters")
  private Double batchSizeLiters;
  @Column(name = "boil_time_minutes")
  private Integer boilTimeMinutes;
  private Double ibu;
  private Double abv;
  private Double og;
  private Double fg;
  private Double efficiency;

  private String equipment;

  @Enumerated(EnumType.STRING)
  @Column(name = "source_format")
  private SourceFormat sourceFormat;

  @Column(name = "source_hash")
  private String sourceHash;

  @Column(length = 4000)
  private String notes;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt = Instant.now();

  @ManyToOne(optional = false)
  private Brewery brewery;

  @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<RecipeFermentable> fermentables = new ArrayList<>();

  @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<RecipeHop> hops = new ArrayList<>();

  @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<RecipeYeast> yeasts = new ArrayList<>();

  @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<RecipeMisc> miscs = new ArrayList<>();

  @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<MashStep> mashSteps = new ArrayList<>();

  public Long getId() { return id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getStyleName() { return styleName; }
  public void setStyleName(String styleName) { this.styleName = styleName; }
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
  public Double getBatchSizeLiters() { return batchSizeLiters; }
  public void setBatchSizeLiters(Double v) { this.batchSizeLiters = v; }
  public Integer getBoilTimeMinutes() { return boilTimeMinutes; }
  public void setBoilTimeMinutes(Integer v) { this.boilTimeMinutes = v; }
  public Double getIbu() { return ibu; }
  public void setIbu(Double ibu) { this.ibu = ibu; }
  public Double getAbv() { return abv; }
  public void setAbv(Double abv) { this.abv = abv; }
  public Double getOg() { return og; }
  public void setOg(Double og) { this.og = og; }
  public Double getFg() { return fg; }
  public void setFg(Double fg) { this.fg = fg; }
  public Double getEfficiency() { return efficiency; }
  public void setEfficiency(Double efficiency) { this.efficiency = efficiency; }
  public String getEquipment() { return equipment; }
  public void setEquipment(String equipment) { this.equipment = equipment; }
  public SourceFormat getSourceFormat() { return sourceFormat; }
  public void setSourceFormat(SourceFormat sourceFormat) { this.sourceFormat = sourceFormat; }
  public String getSourceHash() { return sourceHash; }
  public void setSourceHash(String sourceHash) { this.sourceHash = sourceHash; }
  public String getNotes() { return notes; }
  public void setNotes(String notes) { this.notes = notes; }
  public Instant getCreatedAt() { return createdAt; }
  public Brewery getBrewery() { return brewery; }
  public void setBrewery(Brewery brewery) { this.brewery = brewery; }
}
