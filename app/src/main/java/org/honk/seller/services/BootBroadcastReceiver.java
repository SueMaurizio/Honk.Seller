package org.honk.seller.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.honk.seller.NotificationsHelper;
import org.honk.seller.R;
import org.honk.seller.UI.SetScheduleActivity;
import org.honk.seller.model.DailySchedulePreferences;

import java.util.Calendar;
import java.util.Hashtable;

public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            // Get schedule preferences.
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String settingsString = sharedPreferences.getString(SetScheduleActivity.PREFERENCE_SCHEDULE, "");

            if (settingsString != "") {
                Hashtable<Integer, DailySchedulePreferences> scheduleSettings =
                    new Gson().fromJson(settingsString, TypeToken.getParameterized(Hashtable.class, Integer.class, DailySchedulePreferences.class).getType());

                Calendar now = Calendar.getInstance();
                int currentDayOfWeek = now.get(Calendar.DAY_OF_WEEK);
                DailySchedulePreferences todayPreferences = scheduleSettings.get(currentDayOfWeek);
                int currentYear = now.get(Calendar.YEAR);
                int currentMonth = now.get(Calendar.MONTH);
                int currentDay = now.get(Calendar.DAY_OF_MONTH);
                int nowMillis = now.get(Calendar.HOUR) * 60 * 60 * 1000 + now.get(Calendar.MINUTE) * 60 * 1000;
                if (todayPreferences.workStartTime != null) {
                    // The user works today.
                    Calendar workStart = Calendar.getInstance();
                    workStart.set(currentYear, currentMonth, currentDay, todayPreferences.workStartTime.hours, todayPreferences.workStartTime.minutes, 0);
                    if (now.before(workStart)) {
                        // The working day has not begun yet: schedule location detection for later.
                        this.scheduleLocationDetection(workStart, nowMillis, context);
                    } else {
                        // The working day has already begun: let's verify whether the pause has begun.
                        if (todayPreferences.pauseStartTime != null) {
                            // The user has a pause today.
                            Calendar pauseStart = Calendar.getInstance();
                            pauseStart.set(currentYear, currentMonth, currentDay, todayPreferences.pauseStartTime.hours, todayPreferences.pauseStartTime.minutes, 0);
                            if (now.after(pauseStart)) {
                                // The pause has already begun, let's verify whether it has also ended.
                                Calendar pauseEnd = Calendar.getInstance();
                                pauseEnd.set(currentYear, currentMonth, currentDay, todayPreferences.pauseEndTime.hours, todayPreferences.pauseEndTime.minutes, 0);
                                if (now.after(pauseEnd)) {
                                    // The pause has ended, let's verify whether the working day has ended as well.
                                    this.checkIfWorkingDayHasEnded(currentYear, currentMonth, currentDay, currentDayOfWeek, nowMillis, now, context, scheduleSettings, todayPreferences);
                                } else {
                                    // The pause has not ended, schedule location detection for later.
                                    this.scheduleLocationDetection(pauseEnd, nowMillis, context);
                                }
                            } else {
                                // The pause has not begun yet: location detection must start now.
                                this.scheduleLocationDetection(context, 0);
                            }
                        } else {
                            // No pause today, let's just check whether the working day has ended or not.
                            this.checkIfWorkingDayHasEnded(currentYear, currentMonth, currentDay, currentDayOfWeek, nowMillis, now, context, scheduleSettings, todayPreferences);
                        }
                    }
                } else {
                    // The user does not work today: let's schedule for the next working day.
                    this.scheduleForNextWorkingDay(currentYear, currentMonth, currentDay, currentDayOfWeek, nowMillis, now, context, scheduleSettings);
                }
            }
            else
            {
                // The working schedule is not configured: show a message to the user.
                NotificationsHelper.showNotification(context, context.getString(R.string.ready), context.getString(R.string.setSchedule));
            }
        }
    }

    private void scheduleLocationDetection(Context context, int latencyMillis) {
        latencyMillis = Math.min(latencyMillis, 0);
        NotificationsHelper.showNotification(context, "debug", "Partenza schedulata fra " + latencyMillis + " millisecondi.");
        SchedulerJobService.scheduleJob(context, latencyMillis, latencyMillis);
    }

    private void scheduleLocationDetection(Calendar nextStart, int nowMillis, Context context) {
        int pauseEndMillis = nextStart.get(Calendar.HOUR) * 60 * 60 * 1000 + nextStart.get(Calendar.MINUTE) * 60 * 1000;
        int latency = pauseEndMillis - nowMillis;
        this.scheduleLocationDetection(context, latency);
    }

    private void checkIfWorkingDayHasEnded(
            int currentYear,
            int currentMonth,
            int currentDay,
            int currentDayOfWeek,
            int nowMillis,
            Calendar now,
            Context context,
            Hashtable<Integer, DailySchedulePreferences> scheduleSettings,
            DailySchedulePreferences todayPreferences) {
        Calendar workEnd = Calendar.getInstance();
        workEnd.set(currentYear, currentMonth, currentDay, todayPreferences.workEndTime.hours, todayPreferences.workEndTime.minutes, 0);
        if (now.before(workEnd)) {
            // The working day has not ended yet: location detection must start now.
            this.scheduleLocationDetection(context, 0);
        } else {
            // The working day has ended: schedule for the next working day.
            scheduleForNextWorkingDay(currentYear, currentMonth, currentDay, currentDayOfWeek, nowMillis, now, context, scheduleSettings);
        }
    }

    private void scheduleForNextWorkingDay(
            int currentYear,
            int currentMonth,
            int currentDay,
            int currentDayOfWeek,
            int nowMillis,
            Calendar now,
            Context context,
            Hashtable<Integer, DailySchedulePreferences> scheduleSettings) {
        Calendar nextWorkingDayStart = Calendar.getInstance();
        nextWorkingDayStart.set(currentYear, currentMonth, currentDay);
        nextWorkingDayStart.add(Calendar.DAY_OF_MONTH, 1);
        int nextDayOfWeek = nextWorkingDayStart.get(Calendar.DAY_OF_WEEK);
        DailySchedulePreferences nextDayPreferences = scheduleSettings.get(nextDayOfWeek);
        while(nextDayPreferences.workEndTime != null && nextDayOfWeek != currentDayOfWeek) {
            nextWorkingDayStart.add(Calendar.DAY_OF_MONTH, 1);
            nextDayPreferences = scheduleSettings.get(nextWorkingDayStart.get(Calendar.DAY_OF_WEEK));
        }

        if (nextDayPreferences.workEndTime != null) {
            // I found the next working day: let's schedule location detection.
            int timeToMidnight = (24 * 60 * 60 * 1000) - nowMillis;
            nextWorkingDayStart.add(Calendar.HOUR, nextDayPreferences.workStartTime.hours);
            nextWorkingDayStart.add(Calendar.MINUTE, nextDayPreferences.workStartTime.minutes);
            int timeToNextWorkingDay = (nextWorkingDayStart.get(Calendar.DAY_OF_YEAR) - now.get(Calendar.DAY_OF_YEAR)) * 24 * 60 * 60 * 1000;
            int timeToWorkStart = (nextDayPreferences.workStartTime.hours * 60 * 60 * 1000) + (nextDayPreferences.workStartTime.minutes * 60 * 1000);
            this.scheduleLocationDetection(context, timeToMidnight + timeToNextWorkingDay + timeToWorkStart);
        } else {
            // The working schedule is not configured: show a message to the user.
            NotificationsHelper.showNotification(context, context.getString(R.string.ready), context.getString(R.string.setSchedule));
        }
    }
}
