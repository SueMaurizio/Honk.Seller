package org.honk.seller.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import org.honk.seller.PreferencesHelper;

public class StartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent;
        if (PreferencesHelper.AreScheduleSettingsSet(this.getApplicationContext())) {
            // The schedule settings were not set: launch the first configuration activity.
            intent = new Intent(this, FirstConfigurationActivity.class);
        } else {
            // The schedule settings are set: launch the main activity.
            intent = new Intent(this, MainActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
