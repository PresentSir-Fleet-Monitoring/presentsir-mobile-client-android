package com.rdev.bstrack.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.rdev.bstrack.R;
import com.rdev.bstrack.modals.RegisterStepOne;

public class RegisterOneActivity extends AppCompatActivity {

    private TextInputLayout nameInputLayout, passwordInputLayout, emailInputLayout;
    private TextInputEditText nameEditText, passwordEditText, emailEditText;
    private MaterialButton registerButtonOne;
    private TextView loginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_one);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Views
        nameInputLayout = findViewById(R.id.nameInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        emailInputLayout = findViewById(R.id.emailInputLayout);


        nameEditText = findViewById(R.id.nameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        emailEditText = findViewById(R.id.emailEditText);


        registerButtonOne = findViewById(R.id.registerButtonOne);
        loginText = findViewById(R.id.loginTextView);

        // Set Button Click Listeners
        registerButtonOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegisterOne();
            }
        });

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterOneActivity.this, LoginActivity.class);
                startActivity(intent);            }
        });
    }


    private void handleRegisterOne() {
        // Get Input Values
        String name = nameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        // Validate Input Fields
        if (TextUtils.isEmpty(name)) {
            nameInputLayout.setError("Name is required");
            return;
        } else {
            nameInputLayout.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            passwordInputLayout.setError("Password is required");
            return;
        } else {
            passwordInputLayout.setError(null);
        }

        if (TextUtils.isEmpty(email)) {
            emailInputLayout.setError("Email is required");
            return;
        } else {
            emailInputLayout.setError(null);
        }

        RegisterStepOne stepOne = new RegisterStepOne();
            stepOne.setEmail(email);
            stepOne.setName(name);
            stepOne.setPassword(password);

//        TODO Send with name, password ,email.
        Intent intent = new Intent(RegisterOneActivity.this, RegisterTwoActivity.class);
        intent.putExtra("STEP_ONE",stepOne);
        startActivity(intent);

    }
}