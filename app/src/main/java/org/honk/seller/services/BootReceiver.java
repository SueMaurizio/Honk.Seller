package org.honk.seller.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            /*SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            int id = sharedPreferences.getInt("PREFERENCE_LAST_NOTIFICATION_ID", 0) + 1;
            if (id == Integer.MAX_VALUE) {
                id = 0;
            }

            sharedPreferences.edit().putInt("PREFERENCE_LAST_NOTIFICATION_ID", id).apply();*/
            //LocationBroadcastReceiver.setAlarm(context);

            LocationJobService.scheduleJob(context);
        }
    }
}
