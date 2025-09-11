package com.mythictales.bms.taplist.domain;
public enum KegSize {
    HALF_BARREL(15.5), QUARTER_BARREL(7.75), SIXTEL(5.2);
    private final double gallons;
    KegSize(double gallons){ this.gallons = gallons; }
    public double gallons(){ return gallons; }
    public double ounces(){ return gallons * 128.0; }
}
