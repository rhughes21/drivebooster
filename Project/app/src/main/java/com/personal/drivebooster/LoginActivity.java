package com.personal.drivebooster;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText editTextEmail, editTextPassword;
    Button loginButton;
    Button buttonRegister;
    FirebaseAuth auth;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        if(user != null){
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            LoginActivity.this.finish();
        }

        editTextEmail = findViewById(R.id.email_edit_text);
        editTextPassword = findViewById(R.id.password_input_edit_text);
        buttonRegister = findViewById(R.id.no_account_text);
        loginButton= findViewById(R.id.user_login_button);
        auth = FirebaseAuth.getInstance();
    }

    public void loginUser(View v){
        if(editTextEmail.getText().toString().equals("") || editTextPassword.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
        } else{
            auth.signInWithEmailAndPassword(editTextEmail.getText().toString(), editTextPassword.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(i);
                                Toast.makeText(getApplicationContext(), "Successfully signed in", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(), "Unable to sign in", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }

    public void showRegistrationScreen(View v){
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }
}
