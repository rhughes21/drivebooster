package com.personal.drivebooster;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.personal.drivebooster.Constants.REQUEST_LOCATION;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText editTextUsername, editTextEmail, editTextPassword, editTextPasswordCnf, registerFullAddress;
    FirebaseAuth auth;
    DatabaseReference databaseUsersReference, databaseInstructorsReference, databaseRef, dbUserRef;
    String userType, instructorName, instructorId;
    Spinner userSpinner;
    TextView latitudeTextView, longitudeTextView;
    LocationManager locationManager;
    String apiKey;
    Double longitude, latitude;;
    Button getLocationButton;
    LocationListener locationListener;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String provider;
    ArrayList<String> instructorArray = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        editTextUsername = findViewById(R.id.edit_text_username);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.edit_text_password);
        editTextPasswordCnf = findViewById(R.id.edit_text_passwordCnf);
        latitudeTextView = findViewById(R.id.register_latitude);
        longitudeTextView = findViewById(R.id.register_longitude);
        registerFullAddress = findViewById(R.id.register_full_address);
        auth = FirebaseAuth.getInstance();
        apiKey = getString(R.string.api_key);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        PlacesClient placesClient = Places.createClient(this);

        autoCompleteFragSetUp();
        userSpinner = findViewById(R.id.user_type_spinner);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.user_type_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSpinner.setAdapter(spinnerAdapter);
        userSpinner.setOnItemSelectedListener(this);

    }

    public void autoCompleteFragSetUp(){
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.ADDRESS));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                final LatLng latLng = place.getLatLng();
                assert latLng != null;
                latitudeTextView.setText(String.valueOf(latLng.latitude));
                setLatitude(latLng.latitude);
                setLongitude(latLng.longitude);
                longitudeTextView.setText(String.valueOf(latLng.longitude));
                registerFullAddress.setText(place.getAddress());
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    protected void onStart(){
        super.onStart();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            //getLocation();
        }else{
            askLocationPermission();
        }
    }

    private void askLocationPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_LOCATION){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //getLocation();
            }else{
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    //method called when users clicks login button
    public void showLoginScreen(View v) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }

    //method used to register a new user. Checks whether they are an instructor or pupil
    public void registerUser(View v) {
        final String name = editTextUsername.getText().toString();
        final String email = editTextEmail.getText().toString();
        final String password = editTextPassword.getText().toString();
        final String fullAddress = registerFullAddress.getText().toString();

        if (name.equals("") || email.equals("") || password.equals("")) {
            Toast.makeText(getApplicationContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
        } else {
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                finish();
                                Toast.makeText(getApplicationContext(), "User created", Toast.LENGTH_SHORT).show();
                                if (userType.equals("Pupil")) {
                                    registerPupil(name, email, password, getLatitude(), getLongitude(), fullAddress);
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                } else {
                                    registerInstructor(name, email, password, getLatitude(), getLongitude(), fullAddress);
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), "User could not be created at this time", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    //method used to register a pupil and store the details in firebase
    public void registerPupil(String name, String email, String password, Double latitude, Double longitude, String fullAddress) {

        name = editTextUsername.getText().toString();
        email = editTextEmail.getText().toString();
        password = editTextPassword.getText().toString();
        fullAddress = registerFullAddress.getText().toString();

        instructorName = "not chosen";
        instructorId = "";
        databaseUsersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        Users user_obj = new Users(name, email, password, userType, instructorName, instructorId, getLatitude(), getLongitude(), fullAddress);
        FirebaseUser firebaseUser = auth.getCurrentUser();

        databaseUsersReference.child(firebaseUser.getUid()).setValue(user_obj)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "User details stored", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "User data could not be saved", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //method used to register a new instructor and store the details in firebase
    public void registerInstructor(String name, String email, String password, Double latitude, Double longitude, String fullAddress) {
        name = editTextUsername.getText().toString();
        email = editTextEmail.getText().toString();
        password = editTextPassword.getText().toString();
        fullAddress = registerFullAddress.getText().toString();

        databaseInstructorsReference = FirebaseDatabase.getInstance().getReference().child("Instructors");

        Instructors instructor_obj = new Instructors(name, email, password, userType, getLatitude(), getLongitude(), fullAddress);
        FirebaseUser fbUser = auth.getCurrentUser();

        databaseInstructorsReference.child(fbUser.getUid()).setValue(instructor_obj)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "User details stored", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "User data could not be saved", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    //onItemSelected method for the userType spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        userType = item;
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void getLocation() {
        @SuppressLint("MissingPermission") Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    latitudeTextView.setText(String.valueOf(location.getLatitude()));
                    longitudeTextView.setText(String.valueOf(location.getLongitude()));
                    registerFullAddress.setText(getAddress(location.getLatitude(), location.getLongitude()));
                }
            }
        });

        locationTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                latitudeTextView.setText("Unavailable");
                longitudeTextView.setText("Unavailable");
            }
        });

    }

    private String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                result.append(address.getAddressLine(0));
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        return result.toString();
    }



}

