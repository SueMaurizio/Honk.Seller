package org.honk.seller.UI;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.honk.seller.R;
import org.honk.sharedlibrary.UI.RequirementsCheckerActivity;

public class FirstConfigurationActivity extends RequirementsCheckerActivity {

    private static final int REQUEST_LOCATION_SETTINGS_CHECK = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstconfiguration);
    }

    public void openLoginActivity(View view) {
        // Before entering the login activity, check the requirements for location detection.
        this.checkRequirementsAndPermissions();

        /* Failure to check requirements or permissions causes the activity to close, so if we reach this line,
         * we can proceed to the next activity. */
        Intent openLoginActivityIntent = new Intent(this, LoginActivity.class);
        this.startActivity(openLoginActivityIntent);
    }

    public void close(View view) {
        new AlertDialog.Builder(this)
                .setMessage(this.getString(R.string.comeBackSoon))
                .setTitle(org.honk.sharedlibrary.R.string.alertTitle)
                .setPositiveButton(org.honk.sharedlibrary.R.string.close, (dialog, which) -> {
                    this.finishAffinity();
                })
                .show();
    }

    @Override
    protected void handlePermissionDeniedMessageClick() {
        this.finishAffinity();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_LOCATION_SETTINGS_CHECK) {
            // TODO Handle location settings check result.
        }
    }
}
