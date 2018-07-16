package org.honk.seller;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class NotificationsHelper {

    private static final String PREFERENCE_LAST_NOTIFICATION_ID = "PREFERENCE_LAST_NOTIFICATION_ID";

    public static void showNotification(Context context, String title, String content) {
        showNotification(context, title, content, null, null);
    }

    public static void showNotification(Context context, String title, String content, Intent intent, String intentLabel) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "org.honk.seller")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setLights(0xffffff00, 300, 100);
        if (intent != null) {
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            notificationBuilder.addAction(R.drawable.ic_launcher_background, intentLabel, pendingIntent);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // The notification ID must be unique for each notification.
        notificationManager.notify(getNextNotificationId(context), notificationBuilder.build());
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
