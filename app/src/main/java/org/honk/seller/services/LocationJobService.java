package org.honk.seller.services;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class LocationJobService extends JobService {

    private static final int MINIMUM_LATENCY = 1000 * 60 * 1;
    private static final int MAXIMUM_LATENCY = 1000 * 60 * 3;

    @Override
    public boolean onStartJob(JobParameters params) {
        Context context = this.getApplicationContext();
        Intent service = new Intent(context, LocationService.class);
        context.startService(service);
        scheduleJob(context); // Reschedule the job.
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    public static void scheduleJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, LocationJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(MINIMUM_LATENCY);
        builder.setOverrideDeadline(MAXIMUM_LATENCY);
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }
}