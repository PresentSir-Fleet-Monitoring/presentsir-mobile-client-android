package com.rdev.bstrack.sheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.rdev.bstrack.R;
import com.rdev.bstrack.helpers.ApiClient;
import com.rdev.bstrack.helpers.SecureStorageHelper;
import com.rdev.bstrack.interfaces.AuthService;
import com.rdev.bstrack.modals.LoginResponse;
import com.rdev.bstrack.modals.RequestBody;
import com.rdev.bstrack.modals.User;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class UpdateProfileSheet extends BottomSheetDialogFragment {
    private EditText nameView, contactView, passwordView;
    private View profileSheet;
    private LoginResponse.User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize the class-level profileSheet
        profileSheet = inflater.inflate(R.layout.update_profile_sheet, container, false);

        // Close Button
        profileSheet.findViewById(R.id.closeProfileButton).setOnClickListener(v -> {
            this.dismiss();
        });

        LoginResponse loginResponse = SecureStorageHelper.getLoginResponse(getContext());
        if (loginResponse != null) {
            user = loginResponse.getUser();
        }

        nameView = profileSheet.findViewById(R.id.name);
        contactView = profileSheet.findViewById(R.id.contact);
        passwordView = profileSheet.findViewById(R.id.password);

        if (user != null) {
            nameView.setText(user.getName());
            contactView.setText(user.getContact());
        }

        // Update Button
        profileSheet.findViewById(R.id.updateProfileButton).setOnClickListener(v -> {
            updateAccount();
        });

        return profileSheet;
    }

    public void updateAccount() {
        // Get selected gender from RadioGroup
        RadioGroup genderRadioGroup = profileSheet.findViewById(R.id.genderRadioGroup);
        int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
        if (selectedGenderId == -1) {
            Toast.makeText(getContext(), "Please select a gender.", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton selectedGenderRadioButton = profileSheet.findViewById(selectedGenderId);
        String selectedGender = selectedGenderRadioButton.getText().toString();

        String contact = contactView.getText().toString().trim();
        String name = nameView.getText().toString().trim();
        String password = passwordView.getText().toString().trim();

        if (contact.isEmpty() || name.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "All fields must be filled out.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (user == null) {
            Toast.makeText(getContext(), "User information is unavailable.", Toast.LENGTH_SHORT).show();
            return;
        }

        User newUserData = new User(user.getEmail(), password, name, contact, selectedGender);
        String busId = String.valueOf(user.getBus());
        RequestBody requestBody = new RequestBody(newUserData, busId);

//        ProgressBar progressBar = profileSheet.findViewById(R.id.progressBar);
//        progressBar.setVisibility(View.VISIBLE);

        AuthService authService = ApiClient.getClient().create(AuthService.class);
        Call<Void> call = authService.registerUser(requestBody);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
//                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Update Successful", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Timber.e("Update Error: %s", errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(), "Update failed. Code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
//                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}