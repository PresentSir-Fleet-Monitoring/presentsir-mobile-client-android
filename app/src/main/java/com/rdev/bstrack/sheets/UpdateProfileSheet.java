package com.rdev.bstrack.sheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.rdev.bstrack.R;
import com.rdev.bstrack.helpers.SecureStorageHelper;
import com.rdev.bstrack.modals.LoginResponse;

import java.util.ArrayList;

public class UpdateProfileSheet extends BottomSheetDialogFragment {
    EditText nameView,emailView,contactView,busView,genderView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for the update profile sheet
        View view = inflater.inflate(R.layout.update_profile_sheet, container, false);

        // Close Button
        view.findViewById(R.id.closeProfileButton).setOnClickListener(v -> {
            this.dismiss();
        });
        LoginResponse.User user = SecureStorageHelper.getLoginResponse(getContext()).getUser();


        nameView = view.findViewById(R.id.name);
        emailView = view.findViewById(R.id.email);
        contactView = view.findViewById(R.id.contact);

        if (user != null){
            nameView.setText(user.getName());
            emailView.setText(user.getEmail());
            contactView.setText(user.getContact());
        }

        // Update Button
        view.findViewById(R.id.updateProfileButton).setOnClickListener(v -> {
            // Handle the update button action here

            // Get selected gender from RadioGroup
            RadioGroup genderRadioGroup = view.findViewById(R.id.genderRadioGroup);
            int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
            RadioButton selectedGenderRadioButton = view.findViewById(selectedGenderId);
            String selectedGender = selectedGenderRadioButton.getText().toString();

            // Get selected bus option from Spinner
            Spinner busSpinner = view.findViewById(R.id.bus);
            String selectedBus = busSpinner.getSelectedItem().toString();

            // Display both selected values in a Toast (or handle them as needed)
            Toast.makeText(getContext(), "Selected Gender: " + selectedGender + "\nSelected Bus: " + selectedBus, Toast.LENGTH_LONG).show();
        });

        // Spinner setup for bus options
        Spinner spinner = view.findViewById(R.id.bus);

        // Create a list of items for the dropdown
        ArrayList<String> items = new ArrayList<>();
        items.add("Bus 1");
        items.add("Bus 2");
        items.add("Bus 3");

        // Set up the adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        return view;
    }
}
