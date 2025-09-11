package com.mythictales.bms.taplist.domain;
import jakarta.persistence.*; import java.time.Instant;
@Entity @Table(indexes = { @Index(name="idx_kegevent_placement_time", columnList="placement_id, atTime") })
public class KegEvent {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false) private KegPlacement placement;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private KegEventType type;
    private Double ounces; // nullable for TAP/UNTAP/BLOW
    @Column(nullable=false) private Instant atTime = Instant.now();
    public KegEvent(){}
    public KegEvent(KegPlacement p, KegEventType t, Double ounces){ this.placement=p; this.type=t; this.ounces=ounces; }
    public Long getId(){ return id; }
    public KegPlacement getPlacement(){ return placement; } public void setPlacement(KegPlacement p){ this.placement=p; }
    public KegEventType getType(){ return type; } public void setType(KegEventType type){ this.type=type; }
    public Double getOunces(){ return ounces; } public void setOunces(Double o){ this.ounces=o; }
    public Instant getAtTime(){ return atTime; } public void setAtTime(Instant at){ this.atTime=at; }
}
