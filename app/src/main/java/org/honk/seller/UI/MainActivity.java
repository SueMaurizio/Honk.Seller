package org.honk.seller.UI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.honk.seller.services.LocationBroadcastReceiver;
import org.honk.seller.LocationHelper;
import org.honk.seller.R;
import org.honk.sharedlibrary.UIHelper;

public class MainActivity extends AppCompatActivity {

    private static final int WAIT_TIME_MS = 5000;
    private static final int ACCESS_COARSE_LOCATION_CODE = 0;
    //private static final int TEN_MINUTES = 1000 * 60 * 10;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private LocationHelper locationHelper;

    protected ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView txtLocation = (TextView) findViewById(R.id.txtLocation);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
        int id = sharedPreferences.getInt("PREFERENCE_LAST_NOTIFICATION_ID", 0);
        txtLocation.setText(txtLocation.getText() + " " + id);

        //this.loadLocation();
        //this.displayMessages();
    }

    public void openSetScheduleActivity(View view) {
        Intent openScheduleIntent = new Intent(this, SetScheduleActivity.class);
        this.startActivity(openScheduleIntent);
    }

    private void loadLocation() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Check whether the device supports accessing coarse location.
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_NETWORK)) {
            // Register the listener with the Location Manager to receive location updates.
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                this.getLocation();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    // The user has denied permission, show an explanation and try again.
                    //TODO Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // Request the permission.
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_COARSE_LOCATION_CODE);
                }
            }
        } else {
            // The device is not compatible with this app.
            progressDialog.dismiss();
            UIHelper.showAlert(getString(R.string.featureUnavailableAlertMessage), this);
        }
    }

    private void displayMessages() {
        LocationBroadcastReceiver.setAlarm(this.getBaseContext());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ACCESS_COARSE_LOCATION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.getLocation();
                } else {
                    // User denied permission to access location info.
                    progressDialog.dismiss();
                    UIHelper.showAlert(getString(R.string.permissionDeniedAlertMessage), this);
                }

                return;
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        TextView txtLocation = (TextView) findViewById(R.id.txtLocation);
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // Don't want to use last known location here: if the device is not connected, a message must be shown.
            this.locationHelper = new LocationHelper();
            locationListener = new LocationListener() {

                public void onLocationChanged(Location location) {
                    // Called when a new location is found.
                    locationHelper.SetNewBestLocation(location);
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                public void run() {
                    // Stop listening to location events, to save power.
                    locationManager.removeUpdates(locationListener);
                    progressDialog.dismiss();
                    Location currentBestLocation = locationHelper.getCurrentBestLocation();
                    if (currentBestLocation != null) {
                        txtLocation.setText(currentBestLocation.getLatitude() + " " + currentBestLocation.getLongitude());
                    } else {
                        UIHelper.showAlert(getString(R.string.offlineAlertMessage), MainActivity.this);
                    }
                }
            };
            handler.postDelayed(runnable, WAIT_TIME_MS);
        } else {
            progressDialog.dismiss();
            UIHelper.showAlert(getString(R.string.featureUnavailableAlertMessage), this);
        }
    }
}
