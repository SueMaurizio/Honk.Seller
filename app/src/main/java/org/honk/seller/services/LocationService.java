package org.honk.seller.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.api.ResolvableApiException;

import org.honk.seller.BuildConfig;
import org.honk.seller.NotificationsHelper;
import org.honk.seller.PreferencesHelper;
import org.honk.seller.R;
import org.honk.seller.UI.StopServiceActivity;
import org.honk.seller.model.DailySchedulePreferences;
import org.honk.sharedlibrary.LocationHelper;

import java.util.Calendar;
import java.util.Hashtable;

public class LocationService extends Service {

    private final IBinder locationBinder = new LocationBinder();

    private static final String PREFERENCE_LAST_DAY = "PREFERENCE_LAST_DAY";

    private static final String TAG = "LocationService";

    private static int triesCount = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        checkAndGetCurrentLocation(this.getBaseContext());
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return locationBinder;
    }

    public class LocationBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }
    }

    private static void checkAndGetCurrentLocation(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        int lastDay = sharedPreferences.getInt(PREFERENCE_LAST_DAY, 0);
        if (currentDay != lastDay) {
            // This is the first location detection today: first of all, check if location detection is allowed.
            // Check requirements for location detection.
            if (!LocationHelper.checkLocationSystemFeature(context)) {
                // The device does not support location detection.
                NotificationsHelper.showNotification(context, context.getString(R.string.somethingWentWrong), context.getString(R.string.cannotDetectLocation));
            } else {
                LocationHelper.checkRequirements(
                        context,
                        (locationSettingsResponse) -> {
                            if(BuildConfig.DEBUG) {
                                NotificationsHelper.showNotification(context, "Debug", "LocationService: requirements check successful.");
                            }

                            // Requirements are met: checking user permissions.
                            if (LocationHelper.checkLocationPermission(context))
                            {
                                // Permission was granted: start location detection and display a message to the user.
                                getCurrentLocation(context);

                                Intent stopServiceIntent = new Intent(context, StopServiceActivity.class);

                                // I want to display a notification showing the time stored in settings, not the actual time of the notification, so I try to load it from the app settings.
                                Long exactNotificationTime = null;
                                if (PreferencesHelper.areScheduleSettingsSet(context)) {
                                    Hashtable<Integer, DailySchedulePreferences> scheduleSettings = PreferencesHelper.getScheduleSettings(context);
                                    DailySchedulePreferences todaySchedule = scheduleSettings.get(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
                                    Calendar todayWorkStart = Calendar.getInstance();
                                    todayWorkStart.set(Calendar.HOUR_OF_DAY, todaySchedule.workStartTime.hours);
                                    todayWorkStart.set(Calendar.MINUTE, todaySchedule.workStartTime.minutes);
                                    todayWorkStart.set(Calendar.SECOND, 0);
                                    todayWorkStart.set(Calendar.MILLISECOND, 0);
                                    exactNotificationTime = todayWorkStart.getTimeInMillis();
                                }

                                NotificationsHelper.showNotification(
                                        context, context.getString(R.string.haveANiceDay), context.getString(R.string.locationDetectionStarts), stopServiceIntent, context.getString(R.string.stop), exactNotificationTime);
                                sharedPreferences.edit().putInt(PREFERENCE_LAST_DAY, currentDay).apply();
                            } else {
                                // The user has not given permission to detect location: show a message.
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
                                if(BuildConfig.DEBUG) {
                                    Log.e(TAG, "Exception while starting the app", x);
                                    NotificationsHelper.showNotification(context, "Debug", "Exception in LocationService: " + x.getMessage());
                                }

                                if (triesCount < 3) {
                                    // Retry: cancel all jobs and restart in one minute.
                                    SchedulerJobService.cancelAllJobs(context);
                                    SchedulerJobService.scheduleJob(context, 1000 * 60, 1000 * 60);

                                    triesCount++;

                                    if(BuildConfig.DEBUG) {
                                        NotificationsHelper.showNotification(context, "Debug", "Retrying in 1 minute");
                                    }
                                } else {
                                    // TODO low priority: Allow the user to send feedback.
                                    SchedulerJobService.cancelAllJobs(context);
                                    NotificationsHelper.showNotification(context, context.getString(R.string.somethingWentWrong), context.getString(R.string.cannotDetectLocation));
                                }
                            }
                        });
            }
        } else {
            // This is not the first location detection today: just check user permission.
            // Requirements are met: checking user permissions.
            if (LocationHelper.checkLocationPermission(context))
            {
                // Permission was granted: proceed with location detection.
                getCurrentLocation(context);
            } else {
                // The user has not given permission to detect location: show a message.
                NotificationsHelper.showNotification(context, context.getString(R.string.iNeedHelp), context.getString(R.string.touchToOpenApp));
            }
        }
    }

    private static void getCurrentLocation(Context context) {
        new LocationHelper().getCurrentLocation(context, (location) -> {
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                if(BuildConfig.DEBUG) {
                    NotificationsHelper.showNotification(context, "Debug", "Location: " + location.getLatitude() + " " + location.getLongitude());
                }

                // TODO Send the location to the server.
                // TODO Investigate the behavior of latest Android versions when detecting location while the device is in standby
            }
        });
    }
}
