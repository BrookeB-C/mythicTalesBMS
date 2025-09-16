package com.mythictales.bms.taplist.catalog.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "recipe_fermentable")
public class RecipeFermentable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  private Recipe recipe;

  @Column(nullable = false)
  private String name;

  @Column(name = "amount_kg")
  private Double amountKg;

  @Column(name = "yield_percent") // BeerXML/YIELD
  private Double yieldPercent;

  @Column(name = "color_lovibond") // BeerXML/COLOR
  private Double colorLovibond;

  @Column(name = "late_addition") // BeerSmith Late Extract Additions
  private Boolean lateAddition;

  private String type; // Grain/Extract/Sugar/Adjunct

  public Long getId() {
    return id;
  }

  public Recipe getRecipe() {
    return recipe;
  }

  public void setRecipe(Recipe recipe) {
    this.recipe = recipe;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Double getAmountKg() {
    return amountKg;
  }

  public void setAmountKg(Double amountKg) {
    this.amountKg = amountKg;
  }

  public Double getYieldPercent() {
    return yieldPercent;
  }

  public void setYieldPercent(Double yieldPercent) {
    this.yieldPercent = yieldPercent;
  }

  public Double getColorLovibond() {
    return colorLovibond;
  }

  public void setColorLovibond(Double colorLovibond) {
    this.colorLovibond = colorLovibond;
  }

  public Boolean getLateAddition() {
    return lateAddition;
  }

  public void setLateAddition(Boolean lateAddition) {
    this.lateAddition = lateAddition;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
