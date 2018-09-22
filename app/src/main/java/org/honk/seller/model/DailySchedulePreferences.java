package org.honk.seller.model;

public class DailySchedulePreferences {

    public TimeSpan workStartTime = null;
    public TimeSpan workEndTime = null;
    public TimeSpan breakStartTime = null;
    public TimeSpan breakEndTime = null;

    public  DailySchedulePreferences() {}

    public DailySchedulePreferences(TimeSpan workStartTime, TimeSpan workEndTime, TimeSpan breakStartTime, TimeSpan breakEndTime){
        this.workStartTime = workStartTime;
        this.workEndTime = workEndTime;
        this.breakStartTime = breakStartTime;
        this.breakEndTime = breakEndTime;
    }
}
