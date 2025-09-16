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
}
