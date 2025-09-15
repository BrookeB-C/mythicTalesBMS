package com.mythictales.bms.taplist.catalog.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "recipe_hop")
public class RecipeHop {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  private Recipe recipe;

  @Column(nullable = false)
  private String name;

  @Column(name = "alpha_acid")
  private Double alphaAcid;
  @Column(name = "amount_grams")
  private Double amountGrams;
  @Column(name = "time_minutes")
  private Integer timeMinutes;
  @Column(name = "use_for") // Boil, Dry Hop, Whirlpool, Mash
  private String useFor;
  private String form; // Pellet, Leaf, Plug
  @Column(name = "ibu_contribution")
  private Double ibuContribution;

  public Long getId() { return id; }
  public Recipe getRecipe() { return recipe; }
  public void setRecipe(Recipe recipe) { this.recipe = recipe; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public Double getAlphaAcid() { return alphaAcid; }
  public void setAlphaAcid(Double alphaAcid) { this.alphaAcid = alphaAcid; }
  public Double getAmountGrams() { return amountGrams; }
  public void setAmountGrams(Double amountGrams) { this.amountGrams = amountGrams; }
  public Integer getTimeMinutes() { return timeMinutes; }
  public void setTimeMinutes(Integer timeMinutes) { this.timeMinutes = timeMinutes; }
  public String getUseFor() { return useFor; }
  public void setUseFor(String useFor) { this.useFor = useFor; }
  public String getForm() { return form; }
  public void setForm(String form) { this.form = form; }
  public Double getIbuContribution() { return ibuContribution; }
  public void setIbuContribution(Double ibuContribution) { this.ibuContribution = ibuContribution; }
}
