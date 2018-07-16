package org.honk.seller.services;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class SchedulerJobService extends JobService {

    private static final int MINIMUM_LATENCY = 1000 * 60 * 1;
    private static final int MAXIMUM_LATENCY = 1000 * 60 * 3;

    // Can be set to false if the user forcibly stops the service through the daily notification.
    public static boolean active = true;

    // Can be set if the user decides to stop working for a while.
    public static Calendar pausedUntil = null;

    @Override
    public boolean onStartJob(JobParameters params) {
        Context context = this.getApplicationContext();
        Intent service = new Intent(context, LocationService.class);
        context.startService(service);
        scheduleJob(context);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    public static void scheduleJob(Context context, int minimumLatency, int maximumLatency) {
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

    public static void scheduleJob(Context context) {
        scheduleJob(context, MINIMUM_LATENCY, MAXIMUM_LATENCY);
    }
}