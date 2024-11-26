package com.rdev.bstrack;

import static kotlinx.coroutines.CoroutineScopeKt.CoroutineScope;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.onesignal.OneSignal;
import com.rdev.bstrack.activity.LoginActivity;
import com.rdev.bstrack.databinding.ActivityMainBinding;
import com.rdev.bstrack.fragments.LocateBus;
import com.rdev.bstrack.sheets.AboutSheet;
import com.rdev.bstrack.sheets.ProfileSheet;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private static final int PERMISSION_REQUEST_LOCATION = 1001;
    private long lastClickTime = 0;

    private static FloatingActionButton shareLocationButton;

    private static final int[] IMAGE_RESOURCES = {
            R.drawable.heart_eye,
            R.drawable.pink_heart,
            R.drawable.yellow_heart,
            R.drawable.black_heart,
            R.drawable.fire_heart,
            R.drawable.red_heart
    };

    private String ONESIGNAL_APP_ID = "f841a344-67bc-4525-966e-f5276babe410";
    public static FloatingActionButton getShareLocationButton() {
        return shareLocationButton;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize OneSignal
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);


        String userEmail = "admin@gmail.com" ;
        OneSignal.setExternalUserId(userEmail);
        OneSignal.sendTag("name", "John Doe");
        String s = OneSignal.getDeviceState().getUserId();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        BottomAppBar bottomAppBar = findViewById(R.id.my_bottom_app_bar);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        replaceFragment(new LocateBus());   // Default fragment module or screen ranjit


        shareLocationButton = findViewById(R.id.shareLocationButton);
        shareLocationButton.setOnClickListener(v -> {
            createHeart();

        });

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if(itemId == R.id.account){
                ProfileSheet pf = new ProfileSheet();
                pf.show(getSupportFragmentManager(),pf.getTag());
            }else if(itemId == R.id.about ){
                AboutSheet as = new AboutSheet();
                as.show(getSupportFragmentManager(),as.getTag());
            }else if(itemId == R.id.user_complaints ){

            }
            return true;
        });

        Button logoutButton = findViewById(R.id.logoutButton);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign out the user
//                FirebaseAuth.getInstance().signOut();

                // Redirect to LoginActivity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });


    }

//

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment_container, fragment);
        fragmentTransaction.commit();
    }
    public void createHeart() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime < 300) { // Prevent clicks faster than 300ms
            return;
        }
        lastClickTime = currentTime;

        // Get reference to the root layout
        CoordinatorLayout rootLayout = findViewById(R.id.root_layout);
        if (rootLayout == null) {
            Log.e("MainActivity", "Root layout is null!");
            return;
        }

        // Create a new ShapeableImageView
        ShapeableImageView heartImageView = new ShapeableImageView(this);

        // Set layout parameters for the ShapeableImageView
        CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(
                dpToPx(40), // width in pixels
                dpToPx(40)  // height in pixels
        );

        // Random left or right margin
        Random random = new Random();
        int randomMargin = random.nextInt(dpToPx(100)) - dpToPx(50); // Random margin range: -50dp to +50dp
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        params.setMarginStart(randomMargin); // Apply random margin
        params.bottomMargin = dpToPx(60); // Margin from the bottom

        // Apply layout parameters
        heartImageView.setLayoutParams(params);

        // Randomly select an image from the IMAGE_RESOURCES array
        int randomImageRes = IMAGE_RESOURCES[random.nextInt(IMAGE_RESOURCES.length)];
        // Set the image resource
        heartImageView.setImageResource(randomImageRes);

        // Add the ShapeableImageView to the parent layout
        rootLayout.addView(heartImageView);

        // Create animation set
        AnimationSet animationSet = new AnimationSet(true);

        // Translate animation: Move upward
        TranslateAnimation translateAnimation = new TranslateAnimation(
                0, 0,  // From XDelta, To XDelta
                0, -dpToPx(400) // From YDelta, To YDelta
        );
        translateAnimation.setDuration(2000);

        // Alpha animation: Fade out
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(2000);

        // Add animations to the set
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);

        // Set an animation listener to remove the view after animation
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                // Use Handler to remove the ImageView safely
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        // Ensure the view is still in the layout before removing
                        if (heartImageView.getParent() != null) {
                            rootLayout.removeView(heartImageView);
                        }
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        // Start the animation
        heartImageView.startAnimation(animationSet);
    }


    // Helper method to convert dp to pixels
    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
    @Override
    protected void onDestroy() {
        CoordinatorLayout rootLayout = findViewById(R.id.root_layout);
        rootLayout.removeAllViews();
        super.onDestroy();
    }

}
