package org.honk.seller;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class NotificationsHelper {

    private static final String PREFERENCE_LAST_NOTIFICATION_ID = "PREFERENCE_LAST_NOTIFICATION_ID";

    public static void ShowNotification(Context context, String title, String content) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "org.honk.seller")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // The notification ID must be unique for each notification.
        notificationManager.notify(getNextNotificationId(context), mBuilder.build());
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
