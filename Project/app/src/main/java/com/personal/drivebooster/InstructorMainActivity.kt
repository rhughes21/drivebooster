package com.personal.drivebooster

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.firebase.auth.FirebaseAuth


class InstructorMainActivity :  AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instructor_main)
        var logoutButton = findViewById<Button>(R.id.instructor_logout_button)

        logoutButton.setOnClickListener{
            FirebaseAuth.getInstance().signOut()

            startActivity(Intent(this, LoginActivity::class.java))
            this.finish()
        }

    }
}