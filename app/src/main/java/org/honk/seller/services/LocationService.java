package org.honk.seller.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import org.honk.seller.NotificationsHelper;
import org.honk.seller.R;

import java.util.Calendar;

public class LocationService extends Service {

    private final IBinder mBinder = new LocationBinder();

    private static final String PREFERENCE_LAST_DAY = "PREFERENCE_LAST_DAY";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getLocation(this.getBaseContext());
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        getLocation(this.getBaseContext());
        return mBinder;
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
            NotificationsHelper.ShowNotification(context, context.getString(R.string.haveANiceDay), context.getString(R.string.locationDetectionStarts));
            sharedPreferences.edit().putInt(PREFERENCE_LAST_DAY, currentDay).apply();
        }
    }
}
