package org.honk.seller.model;

public class DailySchedulePreferences {

    public TimeSpan workStartTime = null;
    public TimeSpan workEndTime = null;
    public TimeSpan pauseStartTime = null;
    public TimeSpan pauseEndTime = null;

    public  DailySchedulePreferences() {}

    public DailySchedulePreferences(TimeSpan workStartTime, TimeSpan workEndTime, TimeSpan pauseStartTime, TimeSpan pauseEndTime){
        this.workStartTime = workStartTime;
        this.workEndTime = workEndTime;
        this.pauseStartTime = pauseStartTime;
        this.pauseEndTime = pauseEndTime;
    }
}
