package org.honk.seller;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;

public class NotificationsHelper {

    private static final String PREFERENCE_LAST_NOTIFICATION_ID = "PREFERENCE_LAST_NOTIFICATION_ID";
    private static final String CHANNEL_ID = "org.honk.seller";

    private static NotificationChannel notificationChannel;

    public static void showNotification(Context context, String title, String content) {
        showNotification(context, title, content, null, null);
    }

    public static void showNotification(Context context, String title, String content, Intent intent, String intentLabel) {
        showNotification(context, title, content, intent, intentLabel, null);
    }

    public static void showNotification(Context context, String title, String content, Intent intent, String intentLabel, Long when) {
        showNotification(context, title, content, intent, intentLabel, when, false);
    }

    public static void showNotification(Context context, String title, String content, Intent intent, String intentLabel, Long when, Boolean highPriority) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setContentTitle(title)
                .setContentText(content)
                .setLights(0xffffff00, 300, 100)
                .setAutoCancel(false);

        if (highPriority) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        } else {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        }

        if (when != null) {
            notificationBuilder.setShowWhen(true);
            notificationBuilder.setWhen(when);
        }

        setVibrationPattern(notificationBuilder);

        if (intent != null) {
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            notificationBuilder.addAction(R.drawable.ic_launcher_background, intentLabel, pendingIntent);
        }

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Service.NOTIFICATION_SERVICE);

        Notification notification = notificationBuilder.build();
        notification.category = Notification.CATEGORY_SERVICE ;

        notificationManager.notify(getNextNotificationId(context.getApplicationContext()), notification);
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

    public static void initChannels(Context context) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationChannel = new NotificationChannel(CHANNEL_ID, "Honk Seller", NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setDescription("Honk Seller");
        notificationManager.createNotificationChannel(notificationChannel);
    }

    @TargetApi(26)
    private static void setVibrationPattern(NotificationCompat.Builder notificationBuilder) {
        if (notificationChannel != null) {
            notificationChannel.setVibrationPattern(new long[] { 1000, 300, 200, 300 });
        } else {
            notificationBuilder.setVibrate(new long[] { 1000, 300, 200, 300 });
        }
    }
}
