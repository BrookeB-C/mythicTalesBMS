package com.mythictales.bms.taplist.catalog.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "mash_step")
public class MashStep {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  private Recipe recipe;

  private String name;
  private String type; // Infusion, Temperature, Decoction

  @Column(name = "step_temp_c")
  private Double stepTempC;

  @Column(name = "step_time_minutes")
  private Integer stepTimeMinutes;

  @Column(name = "infuse_amount_liters")
  private Double infuseAmountLiters;

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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Double getStepTempC() {
    return stepTempC;
  }

  public void setStepTempC(Double stepTempC) {
    this.stepTempC = stepTempC;
  }

  public Integer getStepTimeMinutes() {
    return stepTimeMinutes;
  }

  public void setStepTimeMinutes(Integer stepTimeMinutes) {
    this.stepTimeMinutes = stepTimeMinutes;
  }

  public Double getInfuseAmountLiters() {
    return infuseAmountLiters;
  }

  public void setInfuseAmountLiters(Double infuseAmountLiters) {
    this.infuseAmountLiters = infuseAmountLiters;
  }
}
