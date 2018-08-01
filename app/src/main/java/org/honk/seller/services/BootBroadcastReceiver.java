package org.honk.seller.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.honk.seller.NotificationsHelper;

public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            try {
                // Initialize notification channels.
                NotificationsHelper.initChannels(context);

                // First of all, cancel all pending jobs.
                SchedulerJobService.cancelAllJobs(context);

                // Make the job start immediately: it will check itself whether it is work time or not.
                SchedulerJobService.scheduleJob(context);
            }
            catch (Exception x) {
                NotificationsHelper.showNotification(context, "debug", "Eccezione: " + x.getMessage());
            }
        }
    }
}
