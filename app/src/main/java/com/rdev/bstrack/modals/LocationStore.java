package com.rdev.bstrack.modals;

import com.mapmyindia.sdk.maps.geometry.LatLng;

public class LocationStore {

    private static LatLng currentLocation;

    // Set location (called by your service)
    public static void setLocation(double latitude, double longitude) {
        currentLocation = new LatLng(latitude, longitude);
    }

    // Get latest location
    public static LatLng getLocation() {
        return currentLocation;
    }

    // Check if available
    public static boolean hasLocation() {
        return currentLocation != null;
    }
}
