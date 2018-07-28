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
                // First of all, cancel all pending jobs.
                SchedulerJobService.cancelAllJobs(context);

                NotificationsHelper.showNotification(context, "debug", "Inizio schedulazione");
                SchedulerJobService.checkAndSchedule(context);
            }
            catch (Exception x) {
                NotificationsHelper.showNotification(context, "debug", "Eccezione: " + x.getMessage());
            }
        }
    }
}
