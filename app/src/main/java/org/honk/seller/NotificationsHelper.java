package org.honk.seller;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;

public class NotificationsHelper {

    private static final String PREFERENCE_LAST_NOTIFICATION_ID = "PREFERENCE_LAST_NOTIFICATION_ID";

    public static void showNotification(Context context, String title, String content) {
        showNotification(context, title, content, null, null);
    }

    public static void showNotification(Context context, String title, String content, Intent intent, String intentLabel) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "org.honk.seller")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setLights(ContextCompat.getColor(context, R.color.colorPrimary), 500, 200)
                .setAutoCancel(false);
        if (intent != null) {
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            notificationBuilder.addAction(R.drawable.ic_launcher_background, intentLabel, pendingIntent);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        Notification notification = notificationBuilder.build();
        notification.category = Notification.CATEGORY_SERVICE ;

        // The notification ID must be unique for each notification.
        notificationManager.notify(getNextNotificationId(context), notification);
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

    public static void dismissCurrentNotification(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int id = sharedPreferences.getInt(PREFERENCE_LAST_NOTIFICATION_ID, 0);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(id);
    }
}
