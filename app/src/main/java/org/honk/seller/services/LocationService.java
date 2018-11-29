package org.honk.seller.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;

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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setCurrentLocation(this.getBaseContext());
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

    private static void setCurrentLocation(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        int lastDay = sharedPreferences.getInt(PREFERENCE_LAST_DAY, 0);
        if (currentDay != lastDay) {
            // This is the first location detection today: display a message to the user.
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
        }

        new LocationHelper().getCurrentLocation(context, (location) -> {
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                // TODO Send the location to the server.
            }
        });
    }
}
