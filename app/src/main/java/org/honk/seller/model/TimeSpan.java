package org.honk.seller.model;

public class TimeSpan {

    public short hours = 0;
    public short minutes = 0;

    public TimeSpan(int hours, int minutes) {
        this.hours = (short)hours;
        this.minutes = (short)minutes;
    }
}
