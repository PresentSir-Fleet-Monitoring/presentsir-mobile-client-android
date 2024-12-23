package com.rdev.bstrack.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.rdev.bstrack.R;
import com.rdev.bstrack.helpers.ApiClient;
import com.rdev.bstrack.interfaces.ApiService;
import com.rdev.bstrack.interfaces.AuthService;
import com.rdev.bstrack.modals.Buses;
import com.rdev.bstrack.modals.RegisterStepOne;
import com.rdev.bstrack.modals.RequestBody;
import com.rdev.bstrack.modals.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class RegisterTwoActivity extends AppCompatActivity {

    private TextInputLayout contactInputLayout, genderInputLayout, busInputLayout;
    private TextInputEditText contactEditText;
    private AutoCompleteTextView genderDropdown, busDropdown;
    private MaterialButton createAccountButton;
    private ImageView imageButtonToggleTimer;

    private TextView loginText ,backToPrivious;
    private RegisterStepOne registerStepOne;
    private List<Buses> buses;
    private Map<String, Buses> busesWithNameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_two);
        Window window = getWindow();
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

        window.setAttributes(winParams);
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        registerStepOne =  (RegisterStepOne) getIntent().getSerializableExtra("STEP_ONE");

        // Initialize Views
        contactInputLayout = findViewById(R.id.contactInputLayout);
        genderInputLayout = findViewById(R.id.genderInputLayout);
        busInputLayout = findViewById(R.id.busInputLayout);


        contactEditText = findViewById(R.id.contactEditText);

        genderDropdown = findViewById(R.id.genderDropdown);
        busDropdown = findViewById(R.id.busDropdown);

        createAccountButton = findViewById(R.id.createAccountButton);
        loginText = findViewById(R.id.loginTextView);
        backToPrivious = findViewById(R.id.backToStepOneButton);

        buses = new ArrayList<>();
        System.out.println(buses);

        // Load All Buses
        loadBuses();

        // Set Button Click Listeners
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterTwoActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        backToPrivious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterTwoActivity.this, RegisterOneActivity.class);
                startActivity(intent);
            }
        });

    }

    private void setupDropdowns() {
         // Gender Dropdown Options
        String[] genders = {"Male", "Female", "Other"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, genders);
        genderDropdown.setAdapter(genderAdapter);

        busesWithNameId = new HashMap<>();


        List<String> busRouteNames = new ArrayList<>();

        for (int i = 0; i < buses.size(); i++) {

            Buses bus = new Buses(buses.get(i).getRouteName(),buses.get(i).getBusId());

            busesWithNameId.put(bus.getRouteName(),bus);

            String route= bus.getRouteName();

            busRouteNames.add(route);
        }

        // Bus Dropdown Options
        ArrayAdapter<String> busAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, busRouteNames);
        busDropdown.setAdapter(busAdapter);
    }

    private void createAccount() {
        // Get Input Values
        String contact = contactEditText.getText().toString().trim();
        String gender = genderDropdown.getText().toString().trim();
        String bus = busDropdown.getText().toString().trim();

        // Validate Input Fields
        if (TextUtils.isEmpty(contact) || contact.length() != 10 || !TextUtils.isDigitsOnly(contact)) {
            contactInputLayout.setError("Enter a valid 10-digit mobile number");
            return;
        } else {
            contactInputLayout.setError(null);
        }

        List<String> validGenders = Arrays.asList("Male", "Female", "Other");
        if (!validGenders.contains(gender)) {
            genderInputLayout.setError("Please select a valid gender");
            return;
        } else {
            genderInputLayout.setError(null);
        }

        if (TextUtils.isEmpty(bus) || !busesWithNameId.containsKey(bus)) {
            busInputLayout.setError("Please select a valid bus route");
            return;
        } else {
            busInputLayout.setError(null);
        }

        // Get Bus ID
        Buses busID = busesWithNameId.get(bus);

        // If all inputs are valid, proceed with registration logic
        User user = new User(
                registerStepOne.getEmail(),
                registerStepOne.getPassword(),
                registerStepOne.getName(),
                contact,
                gender
        );

        RequestBody requestBody = new RequestBody(user, busID.getBusId());

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        AuthService authService = ApiClient.getClient().create(AuthService.class);
        Call<Void> call = authService.registerUser(requestBody);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(), "Registration failed. Code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RegisterTwoActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBuses() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<Buses>> call = apiService.getAllBuses();
        call.enqueue(new Callback<List<Buses>>() {
            @Override
            public void onResponse(Call<List<Buses>> call, Response<List<Buses>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    buses.clear();
                    buses.addAll(response.body());
                    setupDropdowns(); // Populate dropdown after buses are loaded
                    Timber.tag("Retrofit").d("Buses Loaded: " + buses.size());
                } else {
                    Timber.tag("Retrofit").e("No buses available");
                    Toast.makeText(RegisterTwoActivity.this, "No buses found. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Buses>> call, Throwable t) {
                Timber.tag("Retrofit").e("Request Error: " + t.getMessage());
                Toast.makeText(RegisterTwoActivity.this, "Failed to load buses. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}