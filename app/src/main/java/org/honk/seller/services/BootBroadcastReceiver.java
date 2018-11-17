package org.honk.seller.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnFailureListener;

import org.honk.seller.LocationHelper;
import org.honk.seller.NotificationsHelper;
import org.honk.seller.R;
import org.honk.seller.UI.MainActivity;

public class BootBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "BootBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            try {
                // Initialize notification channels.
                NotificationsHelper.initChannels(context);

                // Check requirements for location detection.
                if (!LocationHelper.checkLocationSystemFeature(context)) {
                    // The device does not support location detection.
                    NotificationsHelper.showNotification(context, context.getString(R.string.somethingWentWrong), context.getString(R.string.cannotDetectLocation));
                } else {
                    LocationHelper.checkRequirements(
                            context,
                            (locationSettingsResponse) -> {
                                // Requirements are met: checking user permissions.
                                if (LocationHelper.checkLocationPermission(context))
                                {
                                    // First of all, cancel all pending jobs.
                                    SchedulerJobService.cancelAllJobs(context);

                                    // Make the job start immediately: it will check itself whether it is work time or not.
                                    SchedulerJobService.scheduleJob(context);
                                } else {
                                    // The user has not given permissions to detect location: show a message.
                                    NotificationsHelper.showNotification(context, context.getString(R.string.iNeedHelp), context.getString(R.string.touchToOpenApp));
                                }
                            },
                            (x) -> {
                                if (x instanceof ResolvableApiException) {
                                    /* Location settings are not satisfied, but this can be fixed
                                     * by showing the user a dialog: make the user start the app. */
                                    NotificationsHelper.showNotification(context, context.getString(R.string.iNeedHelp), context.getString(R.string.touchToOpenApp));
                                } else {
                                    // The exception is not resolvable.
                                    // TODO Allow the user to send feedback.
                                    Log.d(TAG, "Exception while starting the app", x);
                                    NotificationsHelper.showNotification(context, context.getString(R.string.somethingWentWrong), context.getString(R.string.cannotDetectLocation));
                                }
                            });
                }
            }
            catch (Exception x) {
                Log.d(TAG, "Exception while starting the app", x);
                NotificationsHelper.showNotification(context, context.getString(R.string.somethingWentWrong), context.getString(R.string.cannotDetectLocation));
            }
        }
    }
}
