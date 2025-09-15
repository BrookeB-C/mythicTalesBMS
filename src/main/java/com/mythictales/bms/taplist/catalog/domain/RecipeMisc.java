package com.mythictales.bms.taplist.catalog.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "recipe_misc")
public class RecipeMisc {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  private Recipe recipe;

  private String name;
  private String type; // Spice, Fining, Water Agent, etc.
  private Double amount;
  @Column(name = "amount_unit")
  private String amountUnit; // g, ml, etc.
  @Column(name = "use_for")
  private String useFor; // Boil, Fermentation, etc.

  public Long getId() { return id; }
  public Recipe getRecipe() { return recipe; }
  public void setRecipe(Recipe recipe) { this.recipe = recipe; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
  public Double getAmount() { return amount; }
  public void setAmount(Double amount) { this.amount = amount; }
  public String getAmountUnit() { return amountUnit; }
  public void setAmountUnit(String amountUnit) { this.amountUnit = amountUnit; }
  public String getUseFor() { return useFor; }
  public void setUseFor(String useFor) { this.useFor = useFor; }
}
