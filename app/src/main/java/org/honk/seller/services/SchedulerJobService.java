package org.honk.seller.services;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class SchedulerJobService extends JobService {

    private static final int MINIMUM_LATENCY = 1000 * 60 * 1;
    private static final int MAXIMUM_LATENCY = 1000 * 60 * 3;

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
        ComponentName serviceComponent = new ComponentName(context, SchedulerJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(minimumLatency);
        builder.setOverrideDeadline(maximumLatency);
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }

    public static void scheduleJob(Context context) {
        scheduleJob(context, MINIMUM_LATENCY, MAXIMUM_LATENCY);
    }
}