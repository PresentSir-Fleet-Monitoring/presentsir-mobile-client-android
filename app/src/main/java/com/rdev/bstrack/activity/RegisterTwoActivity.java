package com.rdev.bstrack.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.rdev.bstrack.R;
import com.rdev.bstrack.helpers.ApiClient;
import com.rdev.bstrack.interfaces.AppService;
import com.rdev.bstrack.interfaces.AuthService;
import com.rdev.bstrack.modals.Buses;
import com.rdev.bstrack.modals.LoginResponse;
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

public class RegisterTwoActivity extends AppCompatActivity {

    private TextInputLayout contactInputLayout, genderInputLayout, busInputLayout;
    private TextInputEditText contactEditText;
    private AutoCompleteTextView genderDropdown, busDropdown;
    private MaterialButton createAccountButton;
    private TextView loginText ,backToPrivious;
    private RegisterStepOne registerStepOne;
    private List<Buses> buses;
    private Map<String, Buses> busesWithNameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);


        setContentView(R.layout.activity_register_two);
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

        // Populate Dropdowns
        setupDropdowns();

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

        buses = new ArrayList<>();

        List<String> busRouteNames = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            String routeName = "RouteN "+i;
            String bId = "100 "+i;

            Buses bus = new Buses(routeName,bId);

            busesWithNameId.put(bus.getRouteName(),bus);

            String route= bus.getRouteName();

            buses.add(bus);
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
    private void loadBuses(){

        AppService appService = ApiClient.getClient().create(AppService.class);
        Call<Buses> call = appService.getAllBuses();
        call.enqueue(new Callback<Buses>() {
            @Override
            public void onResponse(Call<Buses> call, Response<Buses> response) {
                if (response.isSuccessful()) {
                    Buses data = response.body();
                    long contentLength = response.raw().body().contentLength();

                    for (int i = 0; i < contentLength; i++) {
                        buses.add(data);
                    }

                    Log.d("Retrofit", "ID: " + data.getBusId() + ", Name: " + data.getRouteName());
                } else {
                    Log.e("Retrofit", "Request Failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Buses> call, Throwable t) {
                Log.e("Retrofit", "Request Error: " + t.getMessage());
            }
        });

    }
}