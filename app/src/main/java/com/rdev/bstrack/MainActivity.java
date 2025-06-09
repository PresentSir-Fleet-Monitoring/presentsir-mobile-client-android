package com.rdev.bstrack;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.mapmyindia.sdk.maps.MapmyIndia;
import com.mmi.services.account.MapmyIndiaAccountManager;
import com.onesignal.OneSignal;
import com.rdev.bstrack.activity.LoginActivity;
import com.rdev.bstrack.constants.Constants;
import com.rdev.bstrack.databinding.ActivityMainBinding;
import com.rdev.bstrack.fragments.LocateBus;
import com.rdev.bstrack.helpers.SecureStorageHelper;
import com.rdev.bstrack.modals.LoginResponse;
import com.rdev.bstrack.sheets.AboutSheet;
import com.rdev.bstrack.sheets.ProfileSheet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_LOCATION = 1001;
    private static final int[] IMAGE_RESOURCES = {
            R.drawable.heart_eye,
            R.drawable.pink_heart,
            R.drawable.yellow_heart,
            R.drawable.black_heart,
            R.drawable.fire_heart,
            R.drawable.red_heart
    };

    private DrawerLayout drawerLayout;
    private List<Integer> reminderMeterList;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton shareLocationButton;
    private ImageView speakerButton;
    private ImageView reminderButton;
    private TextView titleTextView;
    private long lastClickTime = 0;

    public static boolean isSpeakerOn = true;
    private Dialog logoutConfirmDialog;
    private Toolbar logoutConfirmToolbar;

    public FloatingActionButton getShareLocationButton(){
        return shareLocationButton;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // MapmyIndia Initialization
        initializeMapmyIndia();
        // Check Login State
        LoginResponse loginResponse = SecureStorageHelper.getLoginResponse(this);
        if (!isUserLoggedIn(loginResponse)) return;

        // UI and SDK Initialization
        initializeOneSignal(loginResponse.getUser());
        initializeUI();
        setupWindow();

    }

    private void initializeMapmyIndia() {
        MapmyIndiaAccountManager.getInstance().setRestAPIKey(Constants.getMapMyIndiaApiKey());
        MapmyIndiaAccountManager.getInstance().setMapSDKKey(Constants.getMapMyIndiaApiKey());
        MapmyIndiaAccountManager.getInstance().setAtlasClientId(Constants.getMapMyIndiaClientId());
        MapmyIndiaAccountManager.getInstance().setAtlasClientSecret(Constants.getMapMyIndiaClientSecret());
        MapmyIndia.getInstance(getApplicationContext());
    }

    private boolean isUserLoggedIn(LoginResponse loginResponse) {
        if (loginResponse == null || loginResponse.getUser() == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return false;
        }
        return true;
    }

    private void setupWindow() {
        Window window = getWindow();
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

        window.setAttributes(winParams);
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }

    private void initializeOneSignal(LoginResponse.User user) {
        OneSignal.initWithContext(this);
        OneSignal.setAppId(Constants.getOnesignalAppId());

        if (user.getEmail() != null) {
            OneSignal.setExternalUserId(user.getEmail());
        }
        if (user.getName() != null) {
            OneSignal.sendTag("name", user.getName());
        }
        if (user.getBus() != null) {
            if (user.getBus().getRouteName() != null) {
                OneSignal.sendTag("routeName", user.getBus().getRouteName());
            }
            OneSignal.sendTag("busID", String.valueOf(user.getBus().getBusId()));
        }
    }

    private void initializeUI() {
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new LocateBus(MainActivity.this)); // Load Default Fragment

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        shareLocationButton = findViewById(R.id.shareLocationButton);
        drawerLayout = findViewById(R.id.drawer_layout);
        speakerButton = findViewById(R.id.speaker_button);
        reminderButton = findViewById(R.id.reminder_button);
        titleTextView = findViewById(R.id.toolbarTitle);

//        NavigationView navigationView = findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this); This feature implemented future

        LoginResponse.User user = SecureStorageHelper.getLoginResponse(this).getUser();
        String userName= user.getName();
        String userRole = user.getRoles().get(0);

        titleTextView.setText("Hey, "+userName);

        setupNavigation(binding);
        setupSpeakerButton();
        setupShareLocationButton(userRole);
        setupLogoutButton();
        setupReminderButton();
    }

    private void setupNavigation(ActivityMainBinding binding) {
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.account) {
                new ProfileSheet().show(getSupportFragmentManager(), null);
            } else if (item.getItemId() == R.id.about) {
                new AboutSheet().show(getSupportFragmentManager(), null);
            }
            return true;
        });
    }

    private void setupSpeakerButton() {
        speakerButton.setOnClickListener(v -> {
            isSpeakerOn = !isSpeakerOn;
            speakerButton.setImageResource(isSpeakerOn ? R.drawable.volume_up_24px : R.drawable.volume_off_24px);
        });
    }

    private void setupReminderButton() {
        reminderButton.setOnClickListener(v -> {
            showBusSelectionDialog();
        });
    }

    private void setupShareLocationButton(String userRole) {
        String visibility = "NO";

        switch (userRole.toLowerCase()) {
            case "user":
                visibility = Constants.getShareLocationButtonVisibleToUser();
                break;
            case "driver":
                visibility = Constants.getShareLocationButtonVisibleToDriver();
                break;
            case "admin":
                visibility = Constants.getShareLocationButtonVisibleToAdmin();
                break;
            case "everyone":
                visibility = Constants.getShareLocationButtonVisibleToEveryone();
                break;
        }

        if ("YES".equalsIgnoreCase(visibility)) {
            shareLocationButton.setVisibility(View.VISIBLE);
        } else {
            shareLocationButton.setVisibility(View.INVISIBLE);
        }

        // Optional click handler:
        // shareLocationButton.setOnClickListener(v -> createHeart());
    }


    private void setupLogoutButton() {
        findViewById(R.id.logoutButton).setOnClickListener(v -> {
//            SecureStorageHelper.clearAllData(getApplicationContext());
//            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            finish();
            showConfirmLogoutDialog();
        });
    }

    private void showBusSelectionDialog() {
        // Initialize the list of reminder times
        reminderMeterList = new ArrayList<>();
        reminderMeterList.add(100);
        reminderMeterList.add(500);
        reminderMeterList.add(1000);
        reminderMeterList.add(2000);
        reminderMeterList.add(3000);

        // Convert the list to a string array for the dialog
        String[] reminderNames = new String[reminderMeterList.size()];
        int checkedItem = 0;
        for (int i = 0; i < reminderMeterList.size(); i++) {
            reminderNames[i] = reminderMeterList.get(i) + " Meter"; // Add a unit or just convert to string
            if (reminderMeterList.get(i) == Constants.getReminderMeter()) {
                checkedItem = i;
            }
        }

        AtomicInteger selectedMeterReminder = new AtomicInteger();

        // Show the dialog
        new MaterialAlertDialogBuilder(this)
                .setTitle("Remind Me when bus is :")
                .setSingleChoiceItems(reminderNames, checkedItem, (dialog, which) -> {
                    selectedMeterReminder.set(reminderMeterList.get(which)); // Get selected value
                })
                .setPositiveButton("OK", (dialog, which) ->{
                    if (selectedMeterReminder.doubleValue()>0){
                        Constants.setReminderMeter(selectedMeterReminder.get());
                        Toast.makeText(this, "I will remind you when bus is : " + selectedMeterReminder + " Meter close to you.", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();

                }).show();
    }

    private void showConfirmLogoutDialog(){
        // Show the dialog
        new MaterialAlertDialogBuilder(this)
                .setTitle("Confirm Logout..")
                .setPositiveButton("Continue", (dialog, which) ->{
                    SecureStorageHelper.clearAllData(getApplicationContext());
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    dialog.dismiss();
                }).show();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment_container, fragment);
        fragmentTransaction.commit();
    }

    public void createHeart() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime < 300) return; // Debounce clicks
        lastClickTime = currentTime;

        CoordinatorLayout rootLayout = findViewById(R.id.root_layout);
        if (rootLayout == null) {
            Log.e(TAG, "Root layout is null!");
            return;
        }

        ShapeableImageView heartImageView = new ShapeableImageView(this);
        CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(dpToPx(45), dpToPx(45));
        Random random = new Random();
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        params.setMarginStart(random.nextInt(dpToPx(100)) - dpToPx(50));
        params.bottomMargin = dpToPx(65);
        heartImageView.setLayoutParams(params);
        heartImageView.setImageResource(IMAGE_RESOURCES[random.nextInt(IMAGE_RESOURCES.length)]);
        rootLayout.addView(heartImageView);

        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, -dpToPx(400));
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        translateAnimation.setDuration(2000);
        alphaAnimation.setDuration(2000);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (heartImageView.getParent() != null) rootLayout.removeView(heartImageView);
                });
            }

            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        heartImageView.startAnimation(animationSet);
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.logout) {
            showConfirmLogoutDialog();
        } else if (id == R.id.nav_home) {
            // Handle the profile action
        }

        drawerLayout.closeDrawer(GravityCompat.END);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }
}
