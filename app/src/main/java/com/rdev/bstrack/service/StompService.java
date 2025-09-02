package com.rdev.bstrack.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;

import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.mapmyindia.sdk.maps.geometry.LatLng;
import com.rdev.bstrack.MainActivity;
import com.rdev.bstrack.R;
import com.rdev.bstrack.constants.Constants;
import com.rdev.bstrack.helpers.TextToSpeechHelper;
import com.rdev.bstrack.modals.LocationData;
import com.rdev.bstrack.modals.LocationStore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.CompletableTransformer;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

public class StompService extends Service {

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private static final String TAG = "StompService";
    private Disposable locationIntervalDisposable;
    private Disposable dispLifecycle;
    private CompositeDisposable compositeDisposable;
    private MapService mapService;

    private FloatingActionButton busLocationButton;
    private FloatingActionButton userLocationButton;

    // Flags
    private Boolean isSendLocationButtonOn = false;
    private Boolean isReceiveLocationButtonOn = false;

    private TextToSpeechHelper ttsHelper;
    private StompClient mStompClient;
    private String USER_EMAIL;
    private String BUS_ID;
    private String AUTH_TOKEN;
    private Context context;
    private MainActivity mainActivity;

    public StompService() {
    }

    public StompService(Context context, String email, String busId, String authToken, MainActivity mainActivity, MapService mapService, FloatingActionButton busLocationButton, FloatingActionButton userLocationButton) {
        this.USER_EMAIL = email;
        this.BUS_ID = busId;
        this.AUTH_TOKEN = authToken;
        this.mainActivity=mainActivity;
        this.mapService =mapService;
        this.context=context;
        this.busLocationButton =busLocationButton;
        this.userLocationButton=userLocationButton;

        // Initialize TextToSpeechHelper
        ttsHelper = new TextToSpeechHelper();
        ttsHelper.initializeTTS(context);


        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, Constants.getWebsocketUrl());
        resetSubscriptions();


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;

                for (Location location : locationResult.getLocations()) {
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();

                    // Save globally
                    LocationStore.setLocation(lat, lng);
                }
            }
        };


        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(5000)
                .setFastestInterval(3000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());

    }
    @Override
    public void onCreate() {
        super.onCreate();
        compositeDisposable = new CompositeDisposable();
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, Constants.getWebsocketUrl());
        resetSubscriptions();
        Log.d(TAG, "StompService created.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForegroundNotification("Present sir is running", "Location Updates");

        return START_STICKY;
    }
    private void startForegroundNotification(String title, String content) {
        String channelId = "stomp_service_channel";
        String channelName = "Stomp Service Channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Notification channel for StompService");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_my_location) // add your custom icon
                .setColor(Color.BLUE)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setContentIntent(android.app.PendingIntent.getActivity(
                        this,
                        0,
                        new Intent(this, MainActivity.class),
                        android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE
                ))
                .build();

        startForeground(1, notification);
    }

    private void updateNotification(String title, String content) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "stomp_service_channel";

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_my_location)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .build();

        if (manager != null) {
            manager.notify(1, notification);
        }
    }
    public void connectStomp(List<StompHeader> headers) {
        mStompClient.withClientHeartbeat(7000).withServerHeartbeat(1000);
        resetSubscriptions();

        dispLifecycle = mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            ttsHelper.speak("You are connected", MainActivity.isSpeakerOn);
                            break;
                        case ERROR:
                            Log.e(TAG, "Server connection error", lifecycleEvent.getException());
                            toast("Server connection error");
                            break;
                        case CLOSED:
                            toast("Stomp connection closed");
                            mainActivity.getShareLocationButton().setImageResource(R.drawable.red_heart);
                            this.busLocationButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                            ttsHelper.speak("You are disconnected", MainActivity.isSpeakerOn);
                            resetSubscriptions();
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            toast("Stomp failed server heartbeat");
                            break;
                    }
                });

        compositeDisposable.add(dispLifecycle);
        mStompClient.connect(headers);
    }

    public void sendLocation() {
        if (isSendLocationButtonOn) {
            isSendLocationButtonOn = false;

            if (locationIntervalDisposable != null && !locationIntervalDisposable.isDisposed()) {
                locationIntervalDisposable.dispose();
            }

            if (mStompClient.isConnected()) {
                mStompClient.disconnect();
                mainActivity.getShareLocationButton().setImageResource(R.drawable.red_heart);
                Log.d(TAG, "Disconnected STOMP client.");
            }

            mainActivity.getShareLocationButton().setImageResource(R.drawable.red_heart);
            ttsHelper.speak("Disconnected.", MainActivity.isSpeakerOn);
            toast("Location sharing stopped.");

        } else {
            isSendLocationButtonOn = true;

            if (mStompClient.isConnected()) {
                mStompClient.disconnect();
                Log.d(TAG, "Existing connection disconnected before reconnecting.");
            }

            List<StompHeader> connectHeaders = new ArrayList<>();
            connectHeaders.add(new StompHeader("Authorization", "Bearer " + AUTH_TOKEN));
            connectHeaders.add(new StompHeader("email", USER_EMAIL));
            connectHeaders.add(new StompHeader("busId", BUS_ID));
            connectHeaders.add(new StompHeader("iam", "sender"));

            mStompClient.connect(connectHeaders);

            mainActivity.getShareLocationButton().setImageResource(R.drawable.black_heart);
            busLocationButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));

            // Start sending location every 5 seconds

            locationIntervalDisposable = Observable.interval(0, 5, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(tick -> {
                        double lat = 0;
                        double lng = 0;
                        if (LocationStore.hasLocation()) {
                            LatLng myLocation = LocationStore.getLocation();
                            lat = myLocation.getLatitude();
                            lng = myLocation.getLongitude();

                            Log.d("MainActivity", "Latest location: " + lat + ", " + lng);
                        }
                        LocationData updatedLocation = new LocationData(
                                USER_EMAIL,
                                BUS_ID,
                                lat,
                                lng
                        );

                        String payload = new Gson().toJson(updatedLocation);

                        mStompClient.send("/app/location", payload)
                                .subscribe(() -> {
                                            Log.d(TAG, "Location sent successfully: " + payload);
                                            mainActivity.runOnUiThread(() -> {
                                                mainActivity.createHeart();  // This method will now be safely called on the main thread
                                            });
                                        },
                                        throwable -> Log.e(TAG, "Error sending location", throwable));
                    }, throwable -> {
                                mainActivity.runOnUiThread(() -> {
                                                mainActivity.getShareLocationButton().setImageResource(R.drawable.red_heart);
                                });
                        Log.e(TAG, "Unhandled error in interval observable", throwable);
                    });
            mainActivity.getShareLocationButton().setImageResource(R.drawable.black_heart);
            toast("Location sharing started.");
            ttsHelper.speak("Location sharing started.", MainActivity.isSpeakerOn);
        }
    }

    public void getBusLocation(String busId) {
        List<StompHeader> connectHeaders = new ArrayList<>();
        connectHeaders.add(new StompHeader("Authorization", "Bearer " + AUTH_TOKEN));
        connectHeaders.add(new StompHeader("email", USER_EMAIL));
        connectHeaders.add(new StompHeader("busId", BUS_ID));
        connectHeaders.add(new StompHeader("iam", "receiver"));

        mStompClient.connect(connectHeaders);

        if (isReceiveLocationButtonOn) {
            isReceiveLocationButtonOn = false;
            mStompClient.disconnect();
            this.busLocationButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
            ttsHelper.speak("Disconnected from bus location updates.", MainActivity.isSpeakerOn);
        } else {
            isReceiveLocationButtonOn = true;

            List<StompHeader> receiveHeaders = new ArrayList<>();
            receiveHeaders.add(new StompHeader("Authorization", "Bearer " + AUTH_TOKEN));
            receiveHeaders.add(new StompHeader("email", USER_EMAIL));
            receiveHeaders.add(new StompHeader("busId", busId));
            receiveHeaders.add(new StompHeader("iam", "receiver"));

            Disposable dispTopic = mStompClient.topic("/topic/location/" + busId, receiveHeaders)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(topicMessage -> {
                        String payload = topicMessage.getPayload();
                        LocationData locationData = new Gson().fromJson(payload, LocationData.class);
                        mapService.showBusLocationOnMap(locationData.getLatitude(), locationData.getLongitude());
                    }, throwable -> Log.e(TAG, "Error on subscribe topic", throwable));

            this.busLocationButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFC0CB")));
            mainActivity.getShareLocationButton().setImageResource(R.drawable.red_heart);
            compositeDisposable.add(dispTopic);
        }
    }

    public void resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        compositeDisposable = new CompositeDisposable();
    }

    private void toast(String text) {
        Timber.tag(TAG).i(text);
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fusedLocationClient.removeLocationUpdates(locationCallback);
        // stop location updates safely
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        stopForeground(true);

        mStompClient.disconnect();
        if (mStompClient != null) {
            mStompClient.disconnect();
        }
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        ttsHelper.stopTTS();
        Log.d(TAG, "StompService destroyed.");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // This is not a bound service
    }

}
