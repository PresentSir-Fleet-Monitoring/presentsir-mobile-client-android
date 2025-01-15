package com.rdev.bstrack.service;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.mapmyindia.sdk.maps.MapView;
import com.mapmyindia.sdk.maps.MapmyIndiaMap;
import com.mapmyindia.sdk.maps.OnMapReadyCallback;
import com.mapmyindia.sdk.maps.annotations.IconFactory;
import com.mapmyindia.sdk.maps.annotations.Marker;
import com.mapmyindia.sdk.maps.annotations.MarkerOptions;
import com.mapmyindia.sdk.maps.camera.CameraPosition;
import com.mapmyindia.sdk.maps.camera.CameraUpdateFactory;
import com.mapmyindia.sdk.maps.geometry.LatLng;
import com.mapmyindia.sdk.maps.location.LocationComponentActivationOptions;
import com.rdev.bstrack.MainActivity;
import com.rdev.bstrack.R;
import com.rdev.bstrack.constants.Constants;
import com.rdev.bstrack.helpers.DistanceCalculator;
import com.rdev.bstrack.helpers.TextToSpeechHelper;

public class MapService implements OnMapReadyCallback {
    private MapView mapView;
    private MapmyIndiaMap mapmyIndiaMap;

    private Marker busMarker;

    private Context context;
    private TextToSpeechHelper ttsHelper;
    private Activity activity;


    public MapService(Context context,Activity activity,MapView mapView, @Nullable Bundle savedInstanceState) {
        this.activity=activity;
        this.mapView = mapView;
        this.context=context;

        // Initialize TextToSpeechHelper
        ttsHelper = new TextToSpeechHelper();
        ttsHelper.initializeTTS(context);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);


    }

    @Override
    public void onMapReady(@NonNull MapmyIndiaMap mapmyIndiaMap) {
        this.mapmyIndiaMap = mapmyIndiaMap;

        mapmyIndiaMap.setMapmyIndiaStyle("DARK");
        mapmyIndiaMap.getStyle(style -> {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                return;
            }
            mapmyIndiaMap.getLocationComponent().activateLocationComponent(
                    LocationComponentActivationOptions.builder(activity, style).build()
            );
            mapmyIndiaMap.getLocationComponent().setLocationComponentEnabled(true);
            mapmyIndiaMap.getUiSettings().setLayerControlEnabled(true);
            mapmyIndiaMap.getUiSettings().setCompassMargins(0,760,70,0);
            mapmyIndiaMap.getUiSettings().setIncreaseScaleThresholdWhenRotating(true);
            mapmyIndiaMap.getUiSettings().setScaleVelocityAnimationEnabled(true);
            mapmyIndiaMap.getUiSettings().setLayerControlEnabled(true);
        });
    }

    @Override
    public void onMapError(int i, String s) {

    }

    public void showUserLocationOnMap() {
        if (mapmyIndiaMap != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (mapmyIndiaMap.getLocationComponent().getLastKnownLocation() != null) {
                    LatLng userLocation = new LatLng(
                            mapmyIndiaMap.getLocationComponent().getLastKnownLocation().getLatitude(),
                            mapmyIndiaMap.getLocationComponent().getLastKnownLocation().getLongitude()
                    );
                    updateMapWithLocation(userLocation);

                } else {
                    Toast.makeText(context, "Waiting for location fix...", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Location permissions not granted.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public LatLng getUserLocation() {
        if (mapmyIndiaMap != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission((context), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (mapmyIndiaMap.getLocationComponent().getLastKnownLocation() != null) {
                    LatLng userLocation = new LatLng(
                            mapmyIndiaMap.getLocationComponent().getLastKnownLocation().getLatitude(),
                            mapmyIndiaMap.getLocationComponent().getLastKnownLocation().getLongitude()
                    );
                    return userLocation;
                } else {
                    Toast.makeText(context, "Waiting for location fix...", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Location permissions not granted.", Toast.LENGTH_SHORT).show();
            }
        }
        return new LatLng(0,0);
    }


    public void showBusLocationOnMap(double busLatitude, double busLongitude) {
        LatLng busLocation = new LatLng(busLatitude, busLongitude);

        if (busMarker == null) {
            busMarker = mapmyIndiaMap.addMarker(new MarkerOptions()
                    .position(busLocation)
                    .icon(IconFactory.getInstance(context).fromResource(R.drawable.bus))
                    .title("Bus")
                    .snippet("I am coming..."));
        } else {
            animateMarker(busMarker, busLocation);
        }

        double distance = DistanceCalculator.calculateDistance(
                mapmyIndiaMap.getLocationComponent().getLastKnownLocation().getLatitude(),
                mapmyIndiaMap.getLocationComponent().getLastKnownLocation().getLongitude(),
                busLatitude, busLongitude
        );


        double averageSpeedKmph = 40;
        double etaMinutes = (distance / averageSpeedKmph) * 60;

//        ttsHelper.speak("The bus is " + (int) distance + " kilometers away and will arrive in  " + (int) etaMinutes + " minutes.", MainActivity.isSpeakerOn);
/*
 - This ensures the logic handles kilometer-based distances while allowing the user to select reminder thresholds in meters.
          -If the current distance is 5.0 kilometers:
                - User selects 500 meters:
                    --Converted reminder distance:
           - The system monitors the current distance.
           - When the current distance reaches 0.5 kilometers or less, it triggers:
                -- "The bus is 0.5 kilometers away and will arrive in X minutes.
*/
        double selectedReminderDistanceKm = Constants.getReminderMeter() / 1000.0;
        double currentDistance = distance; // Current distance in kilometers

        if (currentDistance <= selectedReminderDistanceKm) {

            if (etaMinutes<1){
                ttsHelper.speak(
                        "The bus is " + String.format("%.2f", currentDistance) + " kilometers away and will arrive in few minutes.",
                        MainActivity.isSpeakerOn
                );
            }else {
                ttsHelper.speak(
                        "The bus is " + String.format("%.2f", currentDistance) + " kilometers away and will arrive in " +
                                (int) etaMinutes + " minutes.",
                        MainActivity.isSpeakerOn
                );
            }
        }

    }

    private void soundBusLocation(double busLatitude, double busLongitude) {
        LatLng busLocation = new LatLng(busLatitude, busLongitude);

        double distance = DistanceCalculator.calculateDistance(
                mapmyIndiaMap.getLocationComponent().getLastKnownLocation().getLatitude(),
                mapmyIndiaMap.getLocationComponent().getLastKnownLocation().getLongitude(),
                busLatitude, busLongitude
        );

        double averageSpeedKmph = 40;
        double etaMinutes = (distance / averageSpeedKmph) * 60;

        ttsHelper.speak("The bus is " + (int) distance + " kilometers away and will arrive in  " + (int) etaMinutes + " minutes.",MainActivity.isSpeakerOn);
    }


    private void animateMarker(final Marker marker, final LatLng toPosition) {
        final long duration = 1000;
        final LatLng startPosition = marker.getPosition();

        final long startTime = System.currentTimeMillis();
        mapView.post(new Runnable() {
            @Override
            public void run() {
                long elapsedTime = System.currentTimeMillis() - startTime;
                float fraction = Math.min(1, (float) elapsedTime / duration);
                double lat = startPosition.getLatitude() + fraction * (toPosition.getLatitude() - startPosition.getLatitude());
                double lng = startPosition.getLongitude() + fraction * (toPosition.getLongitude() - startPosition.getLongitude());

                marker.setPosition(new LatLng(lat, lng));

                if (fraction < 1.0) {
                    mapView.post(this);
                } else {
                    marker.setPosition(toPosition);
                }
            }
        });
    }

    public void updateMapWithLocation(LatLng userLocation) {
        setCameraPosition(userLocation, 17,45);
    }

    private void setCameraPosition(LatLng location, int zoom,int tilt) {
        CameraPosition position = new CameraPosition.Builder()
                .target(location)
                .zoom(zoom)
                .tilt(tilt)
                .build();
        mapmyIndiaMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
    }

}
