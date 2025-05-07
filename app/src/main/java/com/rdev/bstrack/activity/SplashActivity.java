package com.rdev.bstrack.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.rdev.bstrack.MainActivity;
import com.rdev.bstrack.R;
import com.rdev.bstrack.service.AppConfig;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Start loading constants and transition after some time
        loadConstantsAndInitializeApp();

        new AppConfig().loadConstants();

    }

    private void loadConstantsAndInitializeApp() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).start(); // Start the background thread to load constants
            }
        }, 3000);  // Show splash screen for 3 seconds (adjust as needed)
    }
}
