package com.personal.drivebooster

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class InstructorHomeFragment : Fragment() {

    var logOutButton : Button? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater!!.inflate(R.layout.instructor_home_fragment, container, false)

        logOutButton = view?.findViewById(R.id.instructor_logout_button)
        val myBookingText = view?.findViewById<TextView>(R.id.instructor_bookings_header)
        logOutButton?.setOnClickListener(View.OnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(activity, LoginActivity::class.java))
            activity!!.finish()
            System.exit(0);
        })

        return view
    }
}