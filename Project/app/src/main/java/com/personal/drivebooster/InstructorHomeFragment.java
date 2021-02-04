package com.personal.drivebooster;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class InstructorHomeFragment extends Fragment implements View.OnClickListener{

    View view;
    Button logOutButton, addTimesButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.instructor_home_fragment, container, false);
        addTimesButton = view.findViewById(R.id.show_times_fragment);
        logOutButton = view.findViewById(R.id.instructor_logout_button);

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
                System.exit(0);
            }
        });

        addTimesButton.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View view) {
        Navigation.findNavController(view).navigate(R.id.action_instructor_home_nav_fragment_to_setInstructorTimesFragment);
    }
}
