package org.honk.seller.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.honk.seller.BuildConfig;
import org.honk.seller.NotificationsHelper;
import org.honk.seller.R;

public class BootBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "BootBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            if(BuildConfig.DEBUG) {
                NotificationsHelper.showNotification(context, "Debug", "Boot broadcast receiver starting");
            }

            try {
                // Initialize notification channels.
                NotificationsHelper.initChannels(context);

                // First of all, cancel all pending jobs.
                SchedulerJobService.cancelAllJobs(context);

                // Make the job start immediately: it will check itself whether it is work time or not.
                SchedulerJobService.scheduleJob(context);
            }
            catch (Exception x) {
                if(BuildConfig.DEBUG) {
                    Log.e(TAG, "Exception while starting the app", x);
                    NotificationsHelper.showNotification(context, "Debug", "Exception in boot broadcast receiver");
                }

                NotificationsHelper.showNotification(context, context.getString(R.string.somethingWentWrong), context.getString(R.string.cannotDetectLocation));
            }
        }
    }
}
