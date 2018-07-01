package org.honk.seller.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;

public class LocationService extends Service {
    private final IBinder mBinder = new MyBinder();
    private int counter = 1;
    private static final String PREFERENCE_LAST_NOTIFICATION_ID = "PREFERENCE_LAST_NOTIFICATION_ID";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getNextNotificationId(this.getBaseContext());
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        getNextNotificationId(this.getBaseContext());
        return mBinder;
    }

    public class MyBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }
    }

    private static int getNextNotificationId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int id = sharedPreferences.getInt(PREFERENCE_LAST_NOTIFICATION_ID, 0) + 1;
        if (id == Integer.MAX_VALUE) {
            id = 0;
        }

        sharedPreferences.edit().putInt(PREFERENCE_LAST_NOTIFICATION_ID, id).apply();
        return id;
    }
}
