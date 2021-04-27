package com.personal.drivebooster

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class SplashActivity : Activity() {
    var handler: Handler? = null
    var user = FirebaseAuth.getInstance().currentUser
    var userId: String? = null
    var userIsPupil = false
    var dbReference: DatabaseReference? = null
    var container: ImageView? = null
    private var animationDrawable: AnimationDrawable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_layout)


        container = findViewById(R.id.splash_images)
        container?.setBackgroundResource(R.drawable.splash_animation)

        animationDrawable = container?.background as AnimationDrawable

    }

    override fun onResume() {
        super.onResume()
        animationDrawable!!.start()
        checkAnimationStatus(50, animationDrawable)
    }

    private fun checkAnimationStatus(time: Int, animationDrawable: AnimationDrawable?) {
        val handler = Handler()
        handler.postDelayed({
            if (animationDrawable!!.current !== animationDrawable!!.getFrame(animationDrawable!!.numberOfFrames - 1))
                checkAnimationStatus(time, animationDrawable)
            else {
                if (user != null) {
                    userId = user!!.uid
                    getUserType()
                } else {
                    val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                    startActivity(intent)
                    this@SplashActivity.finish()
                }
            }
        }, time.toLong())
    }



    //method to check whether the user is a pupil. Checks that the UID exists in the users table
    fun getUserType() {
        dbReference = FirebaseDatabase.getInstance().reference.child("Users")
        dbReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild(userId!!)) {
                    userIsPupil = true
                    val i = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(i)
                    this@SplashActivity.finish()
                } else if (!snapshot.hasChild(userId!!)) {
                    userIsPupil = false
                    val j = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(j)
                    this@SplashActivity.finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}