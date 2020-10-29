package com.personal.drivebooster;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText editTextUsername, editTextEmail, editTextPassword, editTextPasswordCnf;
    Button registerButton;
    Button buttonLogin;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    Spinner userSpinner;
    String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        editTextUsername = findViewById(R.id.edit_text_username);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.edit_text_password);
        editTextPasswordCnf = findViewById(R.id.edit_text_passwordCnf);
        auth = FirebaseAuth.getInstance();
        userSpinner = findViewById(R.id.user_type_spinner);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.user_type_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSpinner.setAdapter(spinnerAdapter);
        userSpinner.setOnItemSelectedListener(this);


    }

    public void showLoginScreen(View v){
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }

    public void registerUser(View v){
        final String name = editTextUsername.getText().toString();
        final String email = editTextEmail.getText().toString();
        final String password = editTextPassword.getText().toString();

        if(name.equals("") || email.equals("") || password.equals("")){
            Toast.makeText(getApplicationContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
        }
        else{
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                Toast.makeText(getApplicationContext(), "User created", Toast.LENGTH_SHORT).show();

                                databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

                                Users user_obj = new Users(editTextUsername.getText().toString(), editTextEmail.getText().toString(), editTextPassword.getText().toString(), userType);
                                FirebaseUser firebaseUser = auth.getCurrentUser();

                                databaseReference.child(firebaseUser.getUid()).setValue(user_obj)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(getApplicationContext(), "User details stored", Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                    Toast.makeText(getApplicationContext(), "User data could not be saved", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                            }else{
                                Toast.makeText(getApplicationContext(), "User could not be created at this time", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        userType = item;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

