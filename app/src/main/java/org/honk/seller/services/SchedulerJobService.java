package org.honk.seller.services;

import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
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

public class SchedulerJobService extends JobService {

    private static final int MINIMUM_LATENCY = 1000 * 60 * 30;
    private static final int MAXIMUM_LATENCY = 1000 * 60 * 35;

    // Can be set to false if the user forcibly stops the service through the daily notification.
    public static boolean active = true;

    // Can be set if the user decides to stop working for a while.
    public static Calendar pausedUntil = null;

    @Override
    public boolean onStartJob(JobParameters params) {
        Context context = this.getApplicationContext();
        try {
            Intent service = new Intent(context, LocationService.class);
            context.startService(service);
            scheduleJob(context);
            return true;
        }
        catch (Exception x) {
            NotificationsHelper.showNotification(context, "debug", "Eccezione: " + x.getMessage());
            return false;
        }
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    public static void scheduleJob(Context context, int minimumLatency, int maximumLatency) {
        try {
            // The job should be scheduled if the service is active and not paused.
            if (active && (pausedUntil == null || pausedUntil.before(Calendar.getInstance()))) {
                // The pause is not set or expired. In either case, it should be set to null.
                pausedUntil = null;

                // Schedule the job.
                ComponentName serviceComponent = new ComponentName(context, SchedulerJobService.class);
                JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
                builder.setMinimumLatency(minimumLatency);
                builder.setOverrideDeadline(maximumLatency);
                JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
                jobScheduler.schedule(builder.build());
            }
        }
        catch (Exception x) {
            NotificationsHelper.showNotification(context, "debug", "Eccezione: " + x.getMessage());
        }
    }

    public static void scheduleJob(Context context) {
        scheduleJob(context, MINIMUM_LATENCY, MAXIMUM_LATENCY);
    }

    public static void checkAndSchedule(Context context) {
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

            // Remember that java months are zero-based.
            int currentMonth = now.get(Calendar.MONTH);
            int currentDay = now.get(Calendar.DAY_OF_MONTH);
            int nowMillis = now.get(Calendar.HOUR_OF_DAY) * 60 * 60 * 1000 + now.get(Calendar.MINUTE) * 60 * 1000;
            if (todayPreferences.workStartTime != null) {
                // The user works today.
                Calendar workStart = Calendar.getInstance();
                workStart.set(currentYear, currentMonth, currentDay, todayPreferences.workStartTime.hours, todayPreferences.workStartTime.minutes, 0);
                if (now.before(workStart)) {
                    // The working day has not begun yet: schedule location detection for later.
                    scheduleLocationDetection(workStart, nowMillis, context);
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
                                checkIfWorkingDayHasEnded(currentYear, currentMonth, currentDay, currentDayOfWeek, nowMillis, now, context, scheduleSettings, todayPreferences);
                            } else {
                                // The pause has not ended, schedule location detection for later.
                                scheduleLocationDetection(pauseEnd, nowMillis, context);
                            }
                        } else {
                            // The pause has not begun yet: location detection must start now.
                            scheduleLocationDetection(context, 0);
                        }
                    } else {
                        // No pause today, let's just check whether the working day has ended or not.
                        checkIfWorkingDayHasEnded(currentYear, currentMonth, currentDay, currentDayOfWeek, nowMillis, now, context, scheduleSettings, todayPreferences);
                    }
                }
            } else {
                // The user does not work today: let's schedule for the next working day.
                scheduleForNextWorkingDay(currentYear, currentMonth, currentDay, currentDayOfWeek, nowMillis, now, context, scheduleSettings);
            }
        }
        else
        {
            // The working schedule is not configured: show a message to the user.
            NotificationsHelper.showNotification(context, context.getString(R.string.ready), context.getString(R.string.setSchedule));
        }
    }

    private static void scheduleLocationDetection(Context context, int latencyMillis) {
        if (latencyMillis < 0) {
            latencyMillis = 0;
        }

        NotificationsHelper.showNotification(context, "debug", "Partenza schedulata fra " + latencyMillis + " millisecondi.");
        SchedulerJobService.scheduleJob(context, latencyMillis, latencyMillis);
    }

    private static void scheduleLocationDetection(Calendar nextStart, int nowMillis, Context context) {
        int pauseEndMillis = nextStart.get(Calendar.HOUR_OF_DAY) * 60 * 60 * 1000 + nextStart.get(Calendar.MINUTE) * 60 * 1000;
        int latency = pauseEndMillis - nowMillis;
        scheduleLocationDetection(context, latency);
    }

    private static void checkIfWorkingDayHasEnded(
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
            scheduleLocationDetection(context, 0);
        } else {
            // The working day has ended: schedule for the next working day.
            scheduleForNextWorkingDay(currentYear, currentMonth, currentDay, currentDayOfWeek, nowMillis, now, context, scheduleSettings);
        }
    }

    private static void scheduleForNextWorkingDay(
            int currentYear,
            int currentMonth,
            int currentDay,
            int currentDayOfWeek,
            int nowMillis,
            Calendar now,
            Context context,
            Hashtable<Integer, DailySchedulePreferences> scheduleSettings) {
        // Compute next day adding one day to the current date and resetting hour and minute.
        Calendar nextWorkingDayStart = Calendar.getInstance();
        nextWorkingDayStart.set(currentYear, currentMonth, currentDay, 0, 0);
        nextWorkingDayStart.add(Calendar.DAY_OF_MONTH, 1);

        // Get the next day of week.
        int nextDayOfWeek = nextWorkingDayStart.get(Calendar.DAY_OF_WEEK);

        // Get the preferences for the next day of week.
        DailySchedulePreferences nextDayPreferences = scheduleSettings.get(nextDayOfWeek);

        /* If the next day is not a working day, cycle until you find a working day.
         * The user may have for some reason configured the app without working days, so I must
         * break the cycle if I encounter the same week day as today. */
        while(nextDayPreferences.workStartTime == null && nextDayOfWeek != currentDayOfWeek) {
            nextWorkingDayStart.add(Calendar.DAY_OF_MONTH, 1);
            nextDayPreferences = scheduleSettings.get(nextWorkingDayStart.get(Calendar.DAY_OF_WEEK));
        }

        if (nextDayPreferences.workStartTime != null) {
            /* I found the next working day: let's schedule location detection.
             * The next location detection will be scheduled after the time between now and midnight,
             * plus the time between midnight and the next working day, plus the time between the first
             * second of the next working day and the instant in which work will start. */
            int timeToMidnight = (24 * 60 * 60 * 1000) - nowMillis;

            /* Compute the number of whole days that separate today from the next working day.
             * For example, if the next working day is tomorrow, it will be 0. Then translate the
             * result in milliseconds. */
            nextWorkingDayStart.add(Calendar.HOUR_OF_DAY, nextDayPreferences.workStartTime.hours);
            nextWorkingDayStart.add(Calendar.MINUTE, nextDayPreferences.workStartTime.minutes);
            int timeToNextWorkingDay = (nextWorkingDayStart.get(Calendar.DAY_OF_YEAR) - (now.get(Calendar.DAY_OF_YEAR) + 1)) * 24 * 60 * 60 * 1000;

            // Compute the time from midnight to the work start instant.
            int timeToWorkStart = (nextDayPreferences.workStartTime.hours * 60 * 60 * 1000) + (nextDayPreferences.workStartTime.minutes * 60 * 1000);

            // Schedule location detection.
            scheduleLocationDetection(context, timeToMidnight + timeToNextWorkingDay + timeToWorkStart);
        } else {
            // There are no working days configured: show a message to the user.
            NotificationsHelper.showNotification(context, context.getString(R.string.ready), context.getString(R.string.setSchedule));
            // TODO: add an action button
        }
    }

    public static void cancelAllJobs(Context context) {
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.cancelAll();
    }
}