package org.honk.seller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            //context.registerReceiver(new LocationBroadcastReceiver(), new IntentFilter("org.honk.seller.ACTION_GET_LOCATION"));
            LocationBroadcastReceiver.setAlarm(context);
        }
    }
}
