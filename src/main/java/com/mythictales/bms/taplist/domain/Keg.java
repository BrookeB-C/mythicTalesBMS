package com.mythictales.bms.taplist.domain;
import jakarta.persistence.*; import jakarta.validation.constraints.*;
@Entity
public class Keg {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false) private Beer beer;
    @Enumerated(EnumType.STRING) private KegSize size = KegSize.HALF_BARREL;
    @Enumerated(EnumType.STRING) private KegStatus status = KegStatus.UNTAPPED;
    @Positive private double totalOunces;
    @PositiveOrZero private double remainingOunces;
    public Keg(){}
    public Keg(Beer beer, KegSize size){
        this.beer=beer; this.size=size; this.totalOunces=size.ounces(); this.remainingOunces=this.totalOunces; this.status=KegStatus.UNTAPPED;
    }
    public Long getId(){ return id; } public void setId(Long id){ this.id=id; }
    public Beer getBeer(){ return beer; } public void setBeer(Beer beer){ this.beer=beer; }
    public KegSize getSize(){ return size; }
    public void setSize(KegSize size){ this.size=size; this.totalOunces=size.ounces(); if (remainingOunces>totalOunces) remainingOunces=totalOunces; }
    public KegStatus getStatus(){ return status; } public void setStatus(KegStatus status){ this.status=status; }
    public double getTotalOunces(){ return totalOunces; } public void setTotalOunces(double v){ totalOunces=v; }
    public double getRemainingOunces(){ return remainingOunces; }
    public void setRemainingOunces(double v){ remainingOunces=Math.max(0, Math.min(totalOunces, v)); if (remainingOunces<=0) status=KegStatus.EMPTY; }
    @Transient public int getFillPercent(){ return totalOunces<=0?0:(int)Math.round((remainingOunces/totalOunces)*100.0); }
}
