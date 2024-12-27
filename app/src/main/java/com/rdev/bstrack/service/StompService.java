package com.rdev.bstrack.service;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.mapmyindia.sdk.maps.geometry.LatLng;
import com.rdev.bstrack.MainActivity;
import com.rdev.bstrack.R;
import com.rdev.bstrack.constants.Constants;
import com.rdev.bstrack.helpers.TextToSpeechHelper;
import com.rdev.bstrack.modals.LocationData;

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

public class StompService  {
    private static final String TAG = "StompService";
    private Disposable locationIntervalDisposable;
    private Disposable dispLifecycle;
    private CompositeDisposable compositeDisposable;
    private MapService mapService;

    private FloatingActionButton busLocationButton;
    private FloatingActionButton userLocationButton;

    // Flags
    private Boolean isSendLocationButtonOn =false;
    private Boolean isReceiveLocationButtonOn =false;


    private TextToSpeechHelper ttsHelper;
    private StompClient mStompClient;
    private String USER_EMAIL;
    private String BUS_ID;
    private String AUTH_TOKEN;
    private Context context;
    private MainActivity mainActivity;

    public StompService(Context context, String email,String busId, String authToken, MainActivity mainActivity,MapService mapService,FloatingActionButton busLocationButton,FloatingActionButton userLocationButton) {
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

    }

    // STOMP CONNECTION METHOD

    public void connectStomp(List<StompHeader> headers) {

        mStompClient.withClientHeartbeat(7000).withServerHeartbeat(1000);

        resetSubscriptions();

        dispLifecycle = mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            ttsHelper.speak("You Are Connected",MainActivity.isSpeakerOn);
                            break;
                        case ERROR:
                            Log.e(TAG, "Server connection error", lifecycleEvent.getException());
                            toast("Server connection error");
                            break;
                        case CLOSED:
                            toast("Stomp connection closed");
                            ttsHelper.speak("You Are Disconnected",MainActivity.isSpeakerOn);
                            this.busLocationButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                            mainActivity.getShareLocationButton().setImageResource(R.drawable.red_heart);
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


    // SEND LOCATION METHOD

    public void sendLocation() {

        if (isSendLocationButtonOn) {
            // Toggle off: Disconnect and stop sending location
            mainActivity.getShareLocationButton().setImageResource(R.drawable.red_heart);
            isSendLocationButtonOn = false;

            if (locationIntervalDisposable != null && !locationIntervalDisposable.isDisposed()) {
                locationIntervalDisposable.dispose();
            }

            if (mStompClient.isConnected()) {
                mStompClient.disconnect();
                Log.d(TAG, "Disconnected STOMP client.");
            }

            ttsHelper.speak("Disconnected.",MainActivity.isSpeakerOn);
            toast("Location sharing stopped.");
        } else {
            // Toggle on: Start sending location with new connection and headers
            isSendLocationButtonOn = true;

            // Disconnect if an existing connection is alive
            if (mStompClient.isConnected()) {
                mStompClient.disconnect();
                Log.d(TAG, "Existing connection disconnected before reconnecting.");
            }

            // Initialize STOMP client with new headers
            List<StompHeader> connectHeaders = new ArrayList<>();
            connectHeaders.add(new StompHeader("Authorization", "Bearer "+AUTH_TOKEN));
            connectHeaders.add(new StompHeader("email", USER_EMAIL));
            connectHeaders.add(new StompHeader("busId", BUS_ID));
            connectHeaders.add(new StompHeader("iam", "sender"));

            mStompClient.connect(connectHeaders);

            // Start sending location every 5 seconds
            mainActivity.getShareLocationButton().setImageResource(R.drawable.black_heart);

            locationIntervalDisposable = Observable.interval(0, 5, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io()) // Runs on IO thread
                    .observeOn(AndroidSchedulers.mainThread()) // Switch to main thread before UI updates
                    .subscribe(tick -> {
                        // Fetch user's latest location
                        LatLng updatedLatLng = mapService.getUserLocation();

                        // Create the updated location object
                        LocationData updatedLocation = new LocationData(
                                USER_EMAIL,
                                BUS_ID,
                                updatedLatLng.getLatitude(),
                                updatedLatLng.getLongitude()
                        );

                        // Convert location data to JSON
                        String payload = new Gson().toJson(updatedLocation);

                        // Send payload using STOMP client
                        mStompClient.send("/app/location", payload)
                                .subscribe(() -> {
                                    // This runs on the main thread due to `observeOn`
                                    Log.d(TAG, "Location sent successfully: " + payload);
                                    mainActivity.runOnUiThread(() -> {
                                        mainActivity.createHeart();  // This method will now be safely called on the main thread
                                    });
                                }, throwable -> {
                                    // Handle errors safely
                                    Log.e(TAG, "Error sending location", throwable);
                                    ttsHelper.speak("Error sending location.",MainActivity.isSpeakerOn);
                                    mainActivity.runOnUiThread(() -> {
                                        mainActivity.getShareLocationButton().setImageResource(R.drawable.red_heart);
                                    });
                                });
                    }, throwable -> {
                        // Global error handling for Observable
                        Timber.tag(TAG).e(throwable, "Unhandled error in interval observable");
                    });

            toast("Location sharing started.");
            ttsHelper.speak("Location sharing started.",MainActivity.isSpeakerOn);
        }
    }


    // GET BUS LOCATION

    public void getBusLocation(String busId) {
        // Initialize STOMP client with new headers
        List<StompHeader> connectHeaders = new ArrayList<>();
        connectHeaders.add(new StompHeader("Authorization", "Bearer "+AUTH_TOKEN));
        connectHeaders.add(new StompHeader("email", USER_EMAIL));
        connectHeaders.add(new StompHeader("busId", BUS_ID));
        connectHeaders.add(new StompHeader("iam", "reciver"));

        mStompClient.connect(connectHeaders);

        if (isReceiveLocationButtonOn) {
            // Toggle off: Disconnect and reset UI
            isReceiveLocationButtonOn = false;
            mStompClient.disconnect();
            ttsHelper.speak("Disconnected from bus location updates.",MainActivity.isSpeakerOn);
            this.busLocationButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
        } else {
            // Toggle on: Connect and subscribe with receiver headers
            isReceiveLocationButtonOn = true;

            // Define headers for receiving (subscriber headers)
            List<StompHeader> receiveHeaders = new ArrayList<>();
            receiveHeaders.add(new StompHeader("Authorization", "Bearer "+AUTH_TOKEN));
            receiveHeaders.add(new StompHeader("email", USER_EMAIL));
            receiveHeaders.add(new StompHeader("busId", busId));
            receiveHeaders.add(new StompHeader("iam", "receiver"));

            this.busLocationButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFC0CB")));
            ttsHelper.speak("Connected to bus",true);

            // Subscribe to the bus location topic
            Disposable dispTopic = mStompClient.topic("/topic/location/" + busId, receiveHeaders)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(topicMessage -> {
                        String payload = topicMessage.getPayload();
                        Gson gson = new Gson();
                        LocationData locationData = gson.fromJson(payload, LocationData.class);
                        Timber.tag(TAG).d("Received: " + locationData.toString());
                        mapService.showBusLocationOnMap(locationData.getLatitude(), locationData.getLongitude());
                    }, throwable -> {
                        Timber.tag(TAG).e(throwable, "Error on subscribe topic");
                        ttsHelper.speak("No location available. Waiting for client...",MainActivity.isSpeakerOn);
                        mainActivity.runOnUiThread(() -> {
                            this.busLocationButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                        });
                        toast("Error: " + throwable.getMessage());
                    });

            // Add subscription to composite disposable
            compositeDisposable.add(dispTopic);
        }
    }



    private LatLng getUserLocation() {
        return mapService.getUserLocation();
    }


    protected CompletableTransformer applySchedulers() {
        return upstream -> upstream
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
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

}
