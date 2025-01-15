package com.rdev.bstrack.modals;

// Helper class to map location data
    public class LocationData {
        private String userEmail;
        private String busId;
        private double latitude;
        private double longitude;

    public LocationData(String userEmail, String busId, double latitude, double longitude) {
        this.userEmail = userEmail;
        this.busId = busId;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}