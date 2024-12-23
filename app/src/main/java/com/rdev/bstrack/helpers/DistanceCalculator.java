package com.rdev.bstrack.helpers;

public class DistanceCalculator {

    /**
     * Calculates the distance between two locations using the Haversine formula.
     *
     * @param userLat Latitude of the user
     * @param userLng Longitude of the user
     * @param busLat Latitude of the bus
     * @param busLng Longitude of the bus
     * @return Distance in kilometers
     */
    public static double calculateDistance(double userLat, double userLng, double busLat, double busLng) {
        final int EARTH_RADIUS = 6371; // Radius of the earth in kilometers

        // Convert latitude and longitude from degrees to radians
        double userLatRad = Math.toRadians(userLat);
        double busLatRad = Math.toRadians(busLat);
        double deltaLat = Math.toRadians(busLat - userLat);
        double deltaLng = Math.toRadians(busLng - userLng);

        // Apply the Haversine formula
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(userLatRad) * Math.cos(busLatRad)
                * Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Calculate the distance in kilometers
        return EARTH_RADIUS * c;
    }
}
