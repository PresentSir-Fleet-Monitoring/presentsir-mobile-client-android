package com.rdev.bstrack.sheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.rdev.bstrack.R;
import com.rdev.bstrack.activity.LoginActivity;
import com.rdev.bstrack.helpers.SecureStorageHelper;
import com.rdev.bstrack.modals.LoginResponse;

public class ProfileSheet extends BottomSheetDialogFragment {
    TextView nameView,emailView,contactView,busView,genderView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for the profile sheet
        View view = inflater.inflate(R.layout.my_profile_sheet, container, true);

        view.findViewById(R.id.closeProfileButton).setOnClickListener(v -> {
            this.dismiss();
        });

        view.findViewById(R.id.editProfileButton).setOnClickListener(v -> {
            this.dismiss();
            UpdateProfileSheet updateProfileSheet = new UpdateProfileSheet();
            updateProfileSheet.show(getParentFragmentManager(),updateProfileSheet.getTag());
        });

        LoginResponse.User user = SecureStorageHelper.getLoginResponse(getContext()).getUser();

         nameView = view.findViewById(R.id.name);
         emailView = view.findViewById(R.id.email);
         contactView = view.findViewById(R.id.contact);
         genderView = view.findViewById(R.id.gender);
         busView = view.findViewById(R.id.bus);

        if (user != null){
            nameView.setText(user.getName());
            emailView.setText(user.getEmail());
            contactView.setText(user.getContact());
            genderView.setText(user.getGender());

            String busInfo = user.getBus().getRouteName()+" ID : "+user.getBus().getBusId();

            busView.setText(busInfo);
        }

        return view;
    }


}
