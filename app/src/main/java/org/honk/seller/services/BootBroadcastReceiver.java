package org.honk.seller.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.honk.seller.NotificationsHelper;
import org.honk.seller.R;
import org.honk.seller.UI.SetScheduleActivity;
import org.honk.seller.model.DailySchedulePreferences;

import java.util.Calendar;
import java.util.Hashtable;

public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            try {
                NotificationsHelper.showNotification(context, "debug", "Inizio schedulazione");
                SchedulerJobService.startScheduling(context);
            }
            catch (Exception x) {
                NotificationsHelper.showNotification(context, "debug", "Eccezione: " + x.getMessage());
            }
        }
    }
}
