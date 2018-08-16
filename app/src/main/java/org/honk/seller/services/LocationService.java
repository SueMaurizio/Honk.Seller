package org.honk.seller.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.honk.seller.NotificationsHelper;
import org.honk.seller.R;
import org.honk.seller.UI.SetScheduleActivity;
import org.honk.seller.UI.StopServiceActivity;
import org.honk.seller.model.DailySchedulePreferences;

import java.util.Calendar;
import java.util.Hashtable;

public class LocationService extends Service {

    private final IBinder locationBinder = new LocationBinder();

    private static final String PREFERENCE_LAST_DAY = "PREFERENCE_LAST_DAY";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getLocation(this.getBaseContext());
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

    private static void getLocation(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        int lastDay = sharedPreferences.getInt(PREFERENCE_LAST_DAY, 0);
        if (currentDay != lastDay) {
            // This is the first location detection today: display a message to the user.
            Intent stopServiceIntent = new Intent(context, StopServiceActivity.class);

            // Here I want to display the time stored in settings, not the actual time of the notification, so I try to load it from the app settings.
            String settingsString = sharedPreferences.getString(SetScheduleActivity.PREFERENCE_SCHEDULE, "");
            Long exactNotificationTime = null;
            if (settingsString != "") {
                Hashtable<Integer, DailySchedulePreferences> scheduleSettings =
                        new Gson().fromJson(settingsString, TypeToken.getParameterized(Hashtable.class, Integer.class, DailySchedulePreferences.class).getType());
                DailySchedulePreferences todaySchedule = scheduleSettings.get(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
                Calendar todayWorkStart = Calendar.getInstance();
                todayWorkStart.set(Calendar.HOUR_OF_DAY, todaySchedule.workStartTime.hours);
                todayWorkStart.set(Calendar.MINUTE, todaySchedule.workStartTime.minutes);
                exactNotificationTime = todayWorkStart.getTimeInMillis();
            }

            NotificationsHelper.showNotification(
                    context, context.getString(R.string.haveANiceDay), context.getString(R.string.locationDetectionStarts), stopServiceIntent, context.getString(R.string.stop), exactNotificationTime);
            sharedPreferences.edit().putInt(PREFERENCE_LAST_DAY, currentDay).apply();
        }
    }
}
