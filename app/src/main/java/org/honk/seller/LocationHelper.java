package org.honk.seller;

import android.location.Location;

public class LocationHelper {

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    protected Location currentBestLocation = null;

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     */
    protected void SetNewBestLocation(Location location) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            this.currentBestLocation = location;
            return;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            this.currentBestLocation = location;
            return;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate ||
            (isNewer && !isLessAccurate) ||
            (isNewer && !isSignificantlyLessAccurate && isFromSameProvider)) {
            this.currentBestLocation = location;
        }
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    public Location getCurrentBestLocation() {
        return this.currentBestLocation;
    }
}
