package com.mythictales.bms.taplist.catalog.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "recipe_yeast")
public class RecipeYeast {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  private Recipe recipe;

  private String name;
  private String laboratory;

  @Column(name = "product_id")
  private String productId;

  private String type; // Ale, Lager, Wheat, Wine, etc.
  private String form; // Liquid, Dry
  private Double attenuation;

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

  public String getLaboratory() {
    return laboratory;
  }

  public void setLaboratory(String laboratory) {
    this.laboratory = laboratory;
  }

  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getForm() {
    return form;
  }

  public void setForm(String form) {
    this.form = form;
  }

  public Double getAttenuation() {
    return attenuation;
  }

  public void setAttenuation(Double attenuation) {
    this.attenuation = attenuation;
  }
}
