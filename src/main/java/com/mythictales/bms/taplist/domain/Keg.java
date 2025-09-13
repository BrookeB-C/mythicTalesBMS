package com.mythictales.bms.taplist.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "uq_keg_brewery_serial", columnNames = {"brewery_id","serialNumber"}))
public class Keg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Beer beer;

    @ManyToOne(optional = false)
    private Brewery brewery; // owning brewery

    // Replaces the old KegSize enum with a typed spec entity
    @ManyToOne(optional = false)
    private KegSizeSpec size;

    @Positive
    private double totalOunces;

    @PositiveOrZero
    private double remainingOunces;

    @Enumerated(EnumType.STRING)
    private KegStatus status = KegStatus.EMPTY;

    @ManyToOne
    private Venue assignedVenue; // when DISTRIBUTED/RECEIVED, assigned to venue

    @Column(nullable = false)
    private String serialNumber;

    @Version
    private long version;

    public Keg() { }

    public Keg(Beer beer, KegSizeSpec size) {
        this.beer = beer;
        this.size = size;
        this.totalOunces = size.getOunces();
        this.remainingOunces = this.totalOunces;
        this.status = KegStatus.EMPTY;
    }

    // Derived for UI
    @Transient
    public int getFillPercent() {
        if (totalOunces <= 0) return 0;
        return (int) Math.round((remainingOunces / totalOunces) * 100.0);
    }

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Beer getBeer() { return beer; }
    public void setBeer(Beer beer) { this.beer = beer; }

    public Brewery getBrewery() { return brewery; }
    public void setBrewery(Brewery brewery) { this.brewery = brewery; }

    public KegSizeSpec getSize() { return size; }
    public void setSize(KegSizeSpec size) {
        this.size = size;
        if (size != null) {
            this.totalOunces = size.getOunces();
            if (remainingOunces > totalOunces) remainingOunces = totalOunces;
        }
    }

    public double getTotalOunces() { return totalOunces; }
    public void setTotalOunces(double totalOunces) { this.totalOunces = totalOunces; }

    public double getRemainingOunces() { return remainingOunces; }
    public void setRemainingOunces(double remainingOunces) { this.remainingOunces = Math.max(0, remainingOunces); }

    public KegStatus getStatus() { return status; }
    public void setStatus(KegStatus status) { this.status = status; }

    public Venue getAssignedVenue() { return assignedVenue; }
    public void setAssignedVenue(Venue assignedVenue) { this.assignedVenue = assignedVenue; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public long getVersion() { return version; }
    public void setVersion(long version) { this.version = version; }
}
