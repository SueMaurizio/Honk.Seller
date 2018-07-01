package org.honk.seller.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class LocationBroadcastReceiver extends BroadcastReceiver {

    private static int executionsCount = 0;
    private static final int WAIT_INTERVAL = 1000 * 60 * 1;
    //public static final String ACTION_GET_LOCATION = "org.honk.seller.GET_LOCATION";
    private static final String PREFERENCE_LAST_NOTIFICATION_ID = "PREFERENCE_LAST_NOTIFICATION_ID";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (executionsCount < 40) {

            // PARTIAL_WAKE_LOCK ensures that the CPU is running, while the screen is allowed to go off.
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "org.honk.seller:WAKE_LOCK");
            wl.acquire();

            executionsCount++;

            /*NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "org.honk.seller")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Titolo messaggio")
                    .setContentText("Messaggio numero " + executionsCount)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(getNextNotificationId(context), mBuilder.build());*/

            getNextNotificationId(context);

            setAlarm(context);

            wl.release();
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

    public static void setAlarm(Context context) {
        // As of API 19, all repeating alarms are inexact. If an application needs precise delivery times then it must use one-time exact alarms.
        //Intent getLocationIntent = new Intent(context, LocationBroadcastReceiver.class);
        Intent getLocationIntent = new Intent("org.honk.seller.ACTION_GET_LOCATION");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, getLocationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = context.getSystemService(AlarmManager.class);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + WAIT_INTERVAL, pendingIntent);
    }
}
