package com.personal.drivebooster;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Locale;

import static com.personal.drivebooster.Constants.REQUEST_LOCATION;

public class InstructorPupilDetailsFragment extends Fragment implements OnMapReadyCallback {

    View view;
    TextView pupilName, pupilAddress, pupilEmail;
    Button sendEmailButton;
    double pupilLat, pupilLong;
    String pupilAddressString;
    DatabaseReference dbRef;
    SupportMapFragment mapFragment;
    GoogleMap mMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.instructor_pupil_details_fragment, container, false);
        initViews();
        mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.pupil_details_map);

        mapFragment.getMapAsync(this);

        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
        return view;
    }

    //initialise views
    public void initViews() {
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_tab_navigation);
        navBar.setVisibility(View.GONE);
        pupilName = view.findViewById(R.id.pupil_details_name);
        pupilEmail = view.findViewById(R.id.pupil_details_email);
        pupilAddress = view.findViewById(R.id.pupil_details_address);

        pupilName.setText(getArguments().getString("pupilName"));
        pupilEmail.setText(getArguments().getString("pupilEmail"));
        pupilAddress.setText(getArguments().getString("pupilAddress"));
        pupilLat = getArguments().getDouble("latitude");
        pupilLong = getArguments().getDouble("longitude");
        pupilAddressString = getArguments().getString("pupilAddress");
        sendEmailButton = view.findViewById(R.id.send_email_button);


    }

    //open system email app and populate with certain data
    protected void sendEmail() {
        Log.i("Send email", "");
        String[] TO = {pupilEmail.getText().toString()};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            getActivity().finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    //if map is ready then show users address
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng address = getLocationFromAddress(getContext(), pupilAddressString);
        mMap.addMarker(new MarkerOptions().position(address).title(pupilName.getText().toString()));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(address, 16));
    }

    //return user location
    public LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p1;

    }

}
