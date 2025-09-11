package com.mythictales.bms.taplist.domain;
import jakarta.persistence.*; import jakarta.validation.constraints.NotBlank;
@Entity
public class Taproom {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @NotBlank private String name;
    @ManyToOne(optional=false) private Brewery brewery;
    public Taproom(){} public Taproom(String name, Brewery brewery){ this.name=name; this.brewery=brewery; }
    public Long getId(){ return id; } public void setId(Long id){ this.id=id; }
    public String getName(){ return name; } public void setName(String name){ this.name=name; }
    public Brewery getBrewery(){ return brewery; } public void setBrewery(Brewery b){ this.brewery=b; }
}
