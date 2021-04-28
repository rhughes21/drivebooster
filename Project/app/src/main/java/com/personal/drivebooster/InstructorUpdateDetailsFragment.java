package com.personal.drivebooster;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.RestrictionsManager.RESULT_ERROR;

public class InstructorUpdateDetailsFragment extends Fragment {

    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    View view;
    EditText userNameView;
    DatabaseReference dbEditRef;
    Button updateDetails;
    FirebaseAuth auth;
    TextView userAddress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.instructor_update_details_fragment, container, false);
        userNameView = view.findViewById(R.id.edit_instructor_name);
        updateDetails = view.findViewById(R.id.update_instructor_details_button);
        auth = FirebaseAuth.getInstance();
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_tab_navigation);
        navBar.setVisibility(View.GONE);
        if (!Places.isInitialized()) {
            Places.initialize(getActivity(), getString(R.string.api_key), Locale.UK);
        }
        PlacesClient placesClient = Places.createClient(getContext());
        updateDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteFragSetUp();

            }
        });
        return view;
    }

    //set up the google maps search fragment
    public void autoCompleteFragSetUp() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields).setCountry("UK")
                .build(getContext());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }


    //check is request code is correct, update details
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("TAG", "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());
                FirebaseUser user = auth.getCurrentUser();
                String myUuid = user.getUid();
                String address = place.getAddress();
                Instructors userObj = new Instructors();
                userObj.updateNameAndAddress(myUuid, userNameView.getText().toString(), address, place.getLatLng().latitude, place.getLatLng().longitude);
                // do query with address

            } else if (resultCode == RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(getContext(), "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                Log.i("aaaa", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
}
