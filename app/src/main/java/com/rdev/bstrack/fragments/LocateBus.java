package com.rdev.bstrack.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapmyindia.sdk.maps.MapView;
import com.rdev.bstrack.MainActivity;
import com.rdev.bstrack.R;
import com.rdev.bstrack.constants.Constants;
import com.rdev.bstrack.helpers.ApiClient;
import com.rdev.bstrack.helpers.SecureStorageHelper;
import com.rdev.bstrack.helpers.TextToSpeechHelper;
import com.rdev.bstrack.interfaces.ApiService;
import com.rdev.bstrack.modals.Buses;
import com.rdev.bstrack.modals.LoginResponse;
import com.rdev.bstrack.service.MapService;
import com.rdev.bstrack.service.StompService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class LocateBus extends Fragment  {
    private static final String TAG = "LocateBus";
    private TextToSpeechHelper ttsHelper;

    private String USER_EMAIL;
    private String BUS_ID;
    private String ROUTE_NAME ;
    private String AUTH_TOKEN ;

    FloatingActionButton shareLocationButton;
    private MapView mapView;

    private FloatingActionButton busLocationButton;
    private FloatingActionButton userLocationButton;
    private FloatingActionButton allBusButton;
    private FloatingActionButton buyMeCofeeButton;
    private MainActivity mainActivity;
    private MapService mapService;
    private Map<String, Buses> busesWithNameId;
    private List<Buses> busesList;
    private Dialog buyMeCoffeeDialog;
    private Toolbar buyMeCoffeeToolbar;

    public LocateBus(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        LoginResponse loginResponse = SecureStorageHelper.getLoginResponse(getContext());
        this.USER_EMAIL =loginResponse.getUser().getEmail();
        this.BUS_ID = String.valueOf(loginResponse.getUser().getBus().getBusId());
        this.ROUTE_NAME= String.valueOf(loginResponse.getUser().getBus().getRouteName());
        this.AUTH_TOKEN= String.valueOf(loginResponse.getToken());

        String role = loginResponse.getUser().getRoles().get(0);

        // Initialize the MapView
        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);  // Important for MapView initialization
        System.out.println("Map fragment called :"+ Constants.getMapMyIndiaApiKey());

        userLocationButton = view.findViewById(R.id.my_loc_button);
        busLocationButton = view.findViewById(R.id.bus_loc_button);
        shareLocationButton = mainActivity.getShareLocationButton();

        allBusButton = view.findViewById(R.id.all_bus_button);
        buyMeCofeeButton = view.findViewById(R.id.timer_button);


        System.out.println(loginResponse);
        setupAllBusButton(role);

        // setting buy_me_coffee visibility default is visible

        if (Objects.equals(Constants.getBuyMeCoffeeButtonVisible(), "NO")){
            buyMeCofeeButton.setVisibility(View.INVISIBLE);
        }else {
            buyMeCofeeButton.setVisibility(View.VISIBLE);
        }

        mapService = new MapService(getContext(),getActivity(),mapView,savedInstanceState);


        // Initialize TextToSpeechHelper
        ttsHelper = new TextToSpeechHelper();
        ttsHelper.initializeTTS(getContext());

        StompService stompService = new StompService(
                getContext(),
                USER_EMAIL,
                BUS_ID,
                AUTH_TOKEN,
                mainActivity,
                mapService,
                busLocationButton,
                userLocationButton
        );

        // Prepare Intent with data
        Intent stompServiceIntent = new Intent(getContext(), StompService.class);
        // Start the service
        getContext().startService(stompServiceIntent);

        stompService.resetSubscriptions();

        loadBuses();

        buyMeCofeeButton.setOnClickListener(v -> {
            showBuyMeCoffeeDialog();
        });

        allBusButton.setOnClickListener(v -> {
            showBusSelectionDialog();
        });


        shareLocationButton.setOnClickListener(v -> {
//            sendLocation();
               stompService.sendLocation(BUS_ID);
        });

        // Set click listeners for buttons
        userLocationButton.setOnClickListener(v -> {
                mapService.showUserLocationOnMap();
                userLocationButton.setImageResource(R.drawable.ic_my_location);
        });


        busLocationButton.setOnClickListener(v ->{

            stompService.getBusLocation(BUS_ID);

        });

        return view;
    }

    private void setupAllBusButton(String userRole) {
        String visibility = "NO";

        switch (userRole.toLowerCase()) {
            case "role_user":
                visibility = Constants.getChangeBusButtonVisibleToUser();
                break;
            case "role_driver":
                visibility = Constants.getChangeBusButtonVisibleToDriver();
                break;
            case "role_admin":
                visibility = Constants.getChangeBusButtonVisibleToAdmin();
                break;
        }

        if ("YES".equalsIgnoreCase(visibility)) {
            allBusButton.setVisibility(View.VISIBLE);
        } else {
            allBusButton.setVisibility(View.GONE); // better than INVISIBLE
        }
    }


    private void showBusSelectionDialog() {

        try {

            // Check if busesList is null or empty
            if (busesList == null) {
                busesList = new ArrayList<>();  // Ensure it's an empty list if loadBuses() returns null
            }

            busesWithNameId = new HashMap<>();

            // Populate the busesWithNameId map with actual bus data
            for (Buses bus : busesList) {
                String route = bus.getRouteName();
                String id = bus.getBusId();
                busesWithNameId.put(route, new Buses(route, id));
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error loading buses", Toast.LENGTH_SHORT).show();
        }

        // Check if busesList is empty before proceeding
        if (busesList.isEmpty()) {
            Toast.makeText(getContext(), "No buses available", Toast.LENGTH_SHORT).show();
            return; // Exit the method if no buses are available
        }

        // Prepare bus names for the dialog
        String[] busNames = new String[busesList.size()];
        int checkedItem = 0;

        // Get the bus names and set the default checked item based on ROUTE_NAME
        for (int i = 0; i < busesList.size(); i++) {
            busNames[i] = busesList.get(i).getRouteName();
            if (busNames[i].equals(ROUTE_NAME)) {
                checkedItem = i;  // Mark the checked item based on ROUTE_NAME
            }
        }

        AtomicInteger selectedBusId = new AtomicInteger();

        // Show the bus selection dialog
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Select Bus")
                .setSingleChoiceItems(busNames, checkedItem, (dialog, which) -> {
                    // When a bus is selected, get the bus ID
                    Buses selectedBus = busesWithNameId.get(busNames[which]);
                    selectedBusId.set(Integer.parseInt(selectedBus.getBusId()));
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    if (selectedBusId.get() >0){
                        Toast.makeText(getContext(), "Selected Bus ID: " + selectedBusId, Toast.LENGTH_SHORT).show();
                        BUS_ID= String.valueOf(selectedBusId);
                        MainActivity.setTilteText("BUS ID : "+String.valueOf(selectedBusId));
                    }
                    dialog.dismiss();
                })
                .show();
    }


    private void showBuyMeCoffeeDialog() {
        // Create a dialog
        buyMeCoffeeDialog = new Dialog(getContext());
        buyMeCoffeeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // No title bar
        buyMeCoffeeDialog.setContentView(R.layout.dialog_webview); // Set custom layout
        buyMeCoffeeDialog.getWindow().setBackgroundDrawableResource(R.drawable.buy_me_cofee_bg);
        buyMeCoffeeToolbar = buyMeCoffeeDialog.findViewById(R.id.toolbar);

        // Initialize the WebView
        WebView webView = buyMeCoffeeDialog.findViewById(R.id.webView);
        ProgressBar progressBar = buyMeCoffeeDialog.findViewById(R.id.progressBar); // Optional: Progress bar for loading

        // Configure the WebView settings
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        // Set WebView client to handle loading and navigation
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Allow navigation within the WebView
                view.loadUrl(url);
                return true;
            }
        });

        // Load the "Buy Me a Coffee" URL
        String buyMeCoffeeUrl = "https://www.buymeacoffee.com/ranjitpatil"; // Replace with your actual URL
        webView.loadUrl(buyMeCoffeeUrl);


        buyMeCoffeeToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyMeCoffeeDialog.dismiss();
            }
        });

        // Show the dialog
        buyMeCoffeeDialog.show();

        // Make the dialog full-screen for a better user experience
        buyMeCoffeeDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public void loadBuses() {
        busesList = new ArrayList<>();

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<Buses>> call = apiService.getAllBuses();
        call.enqueue(new Callback<List<Buses>>() {
            @Override
            public void onResponse(Call<List<Buses>> call, Response<List<Buses>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    busesList.clear();
                    busesList.addAll(response.body());
                    Timber.tag("Retrofit").d("Buses Loaded: " + busesList.size());
                } else {
                    Timber.tag("Retrofit").e("No buses available");
                }
            }

            @Override
            public void onFailure(Call<List<Buses>> call, Throwable t) {
                Timber.tag("Retrofit").e("Request Error: " + t.getMessage());
            }
        });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mapService.showUserLocationOnMap();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
}
