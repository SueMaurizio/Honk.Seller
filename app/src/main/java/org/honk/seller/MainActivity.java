package org.honk.seller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static final int WAIT_TIME_MS = 5000;
    private static final int ACCESS_COARSE_LOCATION_CODE = 0;

    private LocationManager locationManager;
    private LocationListener locationListener;

    protected Location currentBestLocation = null;

    protected ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sto cercando venditori nei paraggi...");
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
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage(R.string.featureUnavailableAlertMessage)
                    .setTitle(R.string.alertTitle)
                    .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {}
                    })
                    .show();
        }
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
                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setMessage(R.string.permissionDeniedAlertMessage)
                            .setTitle(R.string.alertTitle)
                            .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {}
                            })
                            .show();
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
            currentBestLocation = null; //locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            locationListener = new LocationListener() {

                public void onLocationChanged(Location location) {
                    // Called when a new location is found.
                    if (IsNewBestLocation(location)) {
                        currentBestLocation = location;
                    }
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
                    if (currentBestLocation != null) {
                        txtLocation.setText(currentBestLocation.getLatitude() + " " + currentBestLocation.getLongitude());
                    } else {
                        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                                .setMessage(R.string.offlineAlertMessage)
                                .setTitle(R.string.alertTitle)
                                .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {}
                                })
                                .show();
                    }
                }
            };
            handler.postDelayed(runnable, WAIT_TIME_MS);
        } else {
            progressDialog.dismiss();
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage(R.string.featureUnavailableAlertMessage)
                    .setTitle(R.string.alertTitle)
                    .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {}
                    })
                    .show();
        }
    }

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     */
    protected boolean IsNewBestLocation(Location location) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}
