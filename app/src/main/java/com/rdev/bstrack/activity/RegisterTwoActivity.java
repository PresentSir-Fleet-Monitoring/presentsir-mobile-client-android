package com.rdev.bstrack.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.rdev.bstrack.R;
import com.rdev.bstrack.helpers.ApiClient;
import com.rdev.bstrack.interfaces.AuthService;
import com.rdev.bstrack.modals.Buses;
import com.rdev.bstrack.modals.RegisterStepOne;
import com.rdev.bstrack.modals.RequestBody;
import com.rdev.bstrack.modals.User;

import java.util.ArrayList;
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
    List<Buses> buses;
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

        Map<Buses, long> busesWithNameId = new HashMap<>();



        buses = new ArrayList<>();

        List<String> busRouteNames = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            String routeName = "RouteN "+i;

            Buses bus = new Buses(routeName,i+1000);

            String route= bus.getRouteName();

            buses.add(bus);
            busRouteNames.add(route);
        }


        // Bus Dropdown Options
//        String[] buses = {"Bus 1", "Bus 2", "Bus 3"};
        ArrayAdapter<String> busAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, busRouteNames);
        busDropdown.setAdapter(busAdapter);
    }

    private void createAccount() {
        // Get Input Values
        String contact = contactEditText.getText().toString().trim();
        String gender = genderDropdown.getText().toString().trim();
        String bus = busDropdown.getText().toString().trim();

        // Validate Input Fields
        if (TextUtils.isEmpty(contact)) {
            contactInputLayout.setError("Mobile number is required");
            return;
        } else {
            contactInputLayout.setError(null);
        }

        if (TextUtils.isEmpty(gender)) {
            genderInputLayout.setError("Please select a gender");
            return;
        } else {
            genderInputLayout.setError(null);
        }

        if (TextUtils.isEmpty(bus)) {
            busInputLayout.setError("Please select a bus");
            return;
        } else {
            busInputLayout.setError(null);
        }

        // If all inputs are valid, proceed with registration logic
//        Toast.makeText(this, "Registration Successful", Toast.LENGTH_LONG).show();

        User user = new User(
                registerStepOne.getEmail(),
                registerStepOne.getPassword(),
                registerStepOne.getName(),
                contact,
                gender);
        RequestBody requestBody = new RequestBody(user, bus);

        AuthService authService = ApiClient.getClient().create(AuthService.class);
        Call<Void> call = authService.registerUser(requestBody);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
//                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    String regResponse = String.valueOf(response.body());

                    Toast.makeText(getApplicationContext(), "Registered Success", Toast.LENGTH_SHORT).show();

                    System.out.println(response);

                    // Navigate to main activity
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(getApplicationContext(), "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
//                progressBar.setVisibility(View.GONE);
                Toast.makeText(RegisterTwoActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), RegisterOneActivity.class);
                startActivity(intent);
            }
        });
    }

    }