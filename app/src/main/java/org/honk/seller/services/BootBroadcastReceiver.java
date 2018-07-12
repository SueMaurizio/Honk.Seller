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
                DailySchedulePreferences todayPreferences = scheduleSettings.get(now.get(Calendar.DAY_OF_WEEK));
                if (todayPreferences.workStartTime != null) {
                    // The user works today.
                    int currentYear = now.get(Calendar.YEAR);
                    int currentMonth = now.get(Calendar.MONTH);
                    int currentDay = now.get(Calendar.DAY_OF_MONTH);
                    int nowMillis = now.get(Calendar.HOUR) * 60 * 60 * 1000 + now.get(Calendar.MINUTE) * 60 * 1000;
                    Calendar workStart = Calendar.getInstance();
                    workStart.set(currentYear, currentMonth, currentDay, todayPreferences.workStartTime.hours, todayPreferences.workStartTime.minutes, 0);
                    if (now.after(workStart)) {
                        // The working day has not begun yet: schedule location detection for later.
                        int workStartMillis = workStart.get(Calendar.HOUR) * 60 * 60 * 1000 + workStart.get(Calendar.MINUTE) * 60 * 1000;
                        int latency = workStartMillis - nowMillis;
                        this.scheduleLocationDetection(context, latency);
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
                                    Calendar workEnd = Calendar.getInstance();
                                    workEnd.set(currentYear, currentMonth, currentDay, todayPreferences.workEndTime.hours, todayPreferences.workEndTime.minutes, 0);
                                    if (now.before(workEnd)) {
                                        // The working day has not ended yet: location detection must start now.
                                        this.scheduleLocationDetection(context, 0);
                                    }
                                } else {
                                    // The pause has not ended, schedule location detection for later.
                                    int pauseEndMillis = pauseEnd.get(Calendar.HOUR) * 60 * 60 * 1000 + pauseEnd.get(Calendar.MINUTE) * 60 * 1000;
                                    int latency = pauseEndMillis - nowMillis;
                                    this.scheduleLocationDetection(context, latency);
                                }
                            } else {
                                // The pause has not begun yet: location detection must start now.
                                this.scheduleLocationDetection(context, 0);
                            }
                        } else {
                            // No pause today, let's just check whether the working day has ended or not.
                            Calendar workEnd = Calendar.getInstance();
                            workEnd.set(currentYear, currentMonth, currentDay, todayPreferences.workEndTime.hours, todayPreferences.workEndTime.minutes, 0);
                            if (now.before(workEnd)) {
                                // The working day has not ended yet: location detection must start now.
                                this.scheduleLocationDetection(context, 0);
                            }
                        }
                    }
                }
            }
            else
            {
                // The working schedule is not configured: show a message to the user.
                NotificationsHelper.ShowNotification(context, context.getString(R.string.ready), context.getString(R.string.setSchedule));
            }
        }
    }

    private void scheduleLocationDetection(Context context, int latencyMillis) {
        latencyMillis = Math.min(latencyMillis, 0);
        SchedulerJobService.scheduleJob(context, latencyMillis, latencyMillis);
    }
}
