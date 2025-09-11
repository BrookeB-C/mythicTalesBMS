package com.mythictales.bms.taplist.domain;
import jakarta.persistence.*; import jakarta.validation.constraints.Positive;
@Entity
public class Tap {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Positive private int number;
    @ManyToOne private Keg keg;
    @ManyToOne private Taproom taproom;
    @ManyToOne private Bar bar;
    public Tap(){} public Tap(int number){ this.number=number; }
    public Long getId(){ return id; } public void setId(Long id){ this.id=id; }
    public int getNumber(){ return number; } public void setNumber(int number){ this.number=number; }
    public Keg getKeg(){ return keg; } public void setKeg(Keg keg){ this.keg=keg; }
    public Taproom getTaproom(){ return taproom; } public void setTaproom(Taproom taproom){ this.taproom=taproom; }
    public Bar getBar(){ return bar; } public void setBar(Bar bar){ this.bar=bar; }
}
