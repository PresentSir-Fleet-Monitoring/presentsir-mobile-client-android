package com.rdev.bstrack.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

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
import com.rdev.bstrack.modals.LocationStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.LifecycleEvent;
import ua.naiksoftware.stomp.dto.StompHeader;

public class StompService extends Service {

    private static final String TAG = "StompService";
    private static final String CHANNEL_ID = "stomp_service_channel";

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Disposable locationIntervalDisposable;
    private Disposable lifecycleDisposable;

    private StompClient mStompClient;
    private PowerManager.WakeLock wakeLock;

    private TextToSpeechHelper ttsHelper;
    private MapService mapService;
    private MainActivity mainActivity;
    private FloatingActionButton busLocationButton;

    private String USER_EMAIL, BUS_ID, AUTH_TOKEN;
    private boolean isSendLocationButtonOn = false;
    private boolean isReceiveLocationButtonOn = false;

    public StompService() {}

    public StompService(Context context, String email, String busId, String authToken,
                        MainActivity mainActivity, MapService mapService,
                        FloatingActionButton busBtn, FloatingActionButton userBtn) {
        this.USER_EMAIL = email;
        this.BUS_ID = busId;
        this.AUTH_TOKEN = authToken;
        this.mainActivity = mainActivity;
        this.mapService = mapService;
        this.busLocationButton = busBtn;

        ttsHelper = new TextToSpeechHelper();
        ttsHelper.initializeTTS(context);

        initStompClient();
        initLocationClient(context);
    }

    private void initStompClient() {
        // IMPORTANT: Android STOMP requires the /websocket suffix for raw WS
        String url = Constants.getWebsocketUrl();
        if (!url.endsWith("/websocket")) {
            url += "/websocket";
        }

        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url);

        // Heartbeats (10s) prevent Render from killing the idle connection
        mStompClient.withClientHeartbeat(10000).withServerHeartbeat(10000);
    }

    private void setupLifecycle(String mode, String busID) {
        if (lifecycleDisposable != null) lifecycleDisposable.dispose();

        lifecycleDisposable = mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.d(TAG, "✅ STOMP Connection Opened");
                            break;
                        case ERROR:
                            Log.e(TAG, "❌ STOMP Error", lifecycleEvent.getException());
                            handleReconnect(mode, busID);
                            break;
                        case CLOSED:
                            Log.d(TAG, "🔒 STOMP Connection Closed");
                            break;
                    }
                });
        compositeDisposable.add(lifecycleDisposable);
    }

    private void handleReconnect(String mode, String busID) {
        if (isSendLocationButtonOn || isReceiveLocationButtonOn) {
            Log.d(TAG, "Attempting reconnect in 5s...");
            Observable.timer(5, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(tick -> {
                        if (mode.equals("sender")) startSharing(busID);
                        else startReceiving(busID);
                    }, err -> Log.e(TAG, "Reconnect timer failed", err));
        }
    }

    private void initLocationClient(Context context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                for (Location location : locationResult.getLocations()) {
                    LocationStore.setLocation(location.getLatitude(), location.getLongitude());
                }
            }
        };

        LocationRequest req = LocationRequest.create()
                .setInterval(5000)
                .setFastestInterval(3000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        try {
            fusedLocationClient.requestLocationUpdates(req, locationCallback, Looper.getMainLooper());
        } catch (SecurityException e) { Log.e(TAG, "Location Perms Missing", e); }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        startForeground(1, getNotification("Present Sir Active", "Fleet tracking live..."));

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PresentSir:WakeLock");
        if (!wakeLock.isHeld()) wakeLock.acquire(60*60*1000L);

        return START_STICKY;
    }

    public void resetSubscriptions() {
        if (compositeDisposable != null) compositeDisposable.clear();
        if (locationIntervalDisposable != null) locationIntervalDisposable.dispose();
        Log.d(TAG, "Subscriptions and intervals reset.");
    }

    public void sendLocation(String busID) {
        if (isSendLocationButtonOn) {
            isSendLocationButtonOn = false;
            stopSharing();
        } else {
            isSendLocationButtonOn = true;
            startSharing(busID);
        }
    }

    private void startSharing(String busID) {
        resetSubscriptions();
        setupLifecycle("sender", busID);

        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("email", USER_EMAIL));
        headers.add(new StompHeader("busId", busID));
        headers.add(new StompHeader("iam", "sender"));

        mStompClient.connect(headers);

        // UI UPDATES
        mainActivity.runOnUiThread(() -> {
            mainActivity.getShareLocationButton().setImageResource(R.drawable.black_heart);
            mainActivity.toggleHeartAnimation(true); // START HEARTS
        });

        locationIntervalDisposable = Observable.interval(0, 5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tick -> {
                    if (LocationStore.hasLocation() && mStompClient.isConnected()) {
                        LatLng loc = LocationStore.getLocation();
                        Map<String, Object> payload = new HashMap<>();
                        payload.put("userEmail", USER_EMAIL);
                        payload.put("busId", Integer.parseInt(busID));
                        payload.put("latitude", loc.getLatitude());
                        payload.put("longitude", loc.getLongitude());

                        mStompClient.send("/app/bus-location", new Gson().toJson(payload))
                                .subscribe(() -> Log.d(TAG, "Location Sent"),
                                        err -> Log.e(TAG, "Send failed", err));
                    }
                }, err -> Log.e(TAG, "Interval error", err));
    }

    private void stopSharing() {
        resetSubscriptions();
        if (mStompClient.isConnected()) mStompClient.disconnect();

        // UI UPDATES
        mainActivity.runOnUiThread(() -> {
            mainActivity.getShareLocationButton().setImageResource(R.drawable.red_heart);
            mainActivity.toggleHeartAnimation(false); // STOP HEARTS
        });

        ttsHelper.speak("Sharing stopped", MainActivity.isSpeakerOn);
    }
    public void getBusLocation(String busId) {
        if (isReceiveLocationButtonOn) {
            isReceiveLocationButtonOn = false;
            mStompClient.disconnect();
            busLocationButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        } else {
            isReceiveLocationButtonOn = true;
            startReceiving(busId);
        }
    }

    private void startReceiving(String busId) {
        resetSubscriptions();
        setupLifecycle("receiver", busId);

        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("email", USER_EMAIL)); // Added email for auth
        headers.add(new StompHeader("iam", "receiver"));

        // 1. First, connect
        mStompClient.connect(headers);

        // 2. Only subscribe once the lifecycle says "OPENED"
        Disposable topicWaiter = mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    if (lifecycleEvent.getType() == LifecycleEvent.Type.OPENED) {

                        Log.d(TAG, "Connected! Now subscribing to bus: " + busId);

                        // 3. Now subscribe to the topic
                        Disposable topicDisp = mStompClient.topic("/topic/bus-location/" + busId)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(msg -> {
                                    Log.d(TAG, "📥 RECEIVED: " + msg.getPayload());
                                    Map data = new Gson().fromJson(msg.getPayload(), Map.class);

                                    // Ultra-safe extraction
                                    double lat = 0, lng = 0;
                                    try {
                                        lat = Double.parseDouble(data.containsKey("latitude") ? data.get("latitude").toString() : data.get("lat").toString());
                                        lng = Double.parseDouble(data.containsKey("longitude") ? data.get("longitude").toString() : data.get("lng").toString());
                                    } catch (Exception e) { Log.e(TAG, "Data parse error", e); }

                                    if (lat != 0) {
                                        mapService.showBusLocationOnMap(lat, lng);
                                    }
                                }, err -> Log.e(TAG, "Topic Error", err));

                        compositeDisposable.add(topicDisp);
                    }
                });

        compositeDisposable.add(topicWaiter);
        busLocationButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFC0CB")));
    }

    private Notification getNotification(String title, String content) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title).setContentText(content)
                .setSmallIcon(R.drawable.ic_my_location).setOngoing(true).build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(CHANNEL_ID, "Fleet Tracking", NotificationManager.IMPORTANCE_LOW);
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(ch);
        }
    }

    @Override
    public void onDestroy() {
        resetSubscriptions();
        if (wakeLock != null && wakeLock.isHeld()) wakeLock.release();
        if (mStompClient != null) mStompClient.disconnect();
        fusedLocationClient.removeLocationUpdates(locationCallback);
        super.onDestroy();
    }

    @Override public IBinder onBind(Intent intent) { return null; }
}