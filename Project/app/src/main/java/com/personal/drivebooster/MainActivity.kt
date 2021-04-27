package com.personal.drivebooster

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*

var userIsPupil : Boolean = false

var userId: String? = null
var user = FirebaseAuth.getInstance().currentUser
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(!user!!.equals(null)){
            Log.i("User id", user!!.uid)
            userId = user!!.uid
        }

        setUpNavigation()
        if (supportActionBar != null) {
            val actionBar: ActionBar? = supportActionBar
            actionBar?.setDisplayHomeAsUpEnabled(false)
        }
    }

    override fun onResume() {
        super.onResume()
        setUpNavigation()

    }

    //show links to gov.uk is user is a pupil
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return if(userIsPupil){
            val inflater: MenuInflater = menuInflater
            inflater.inflate(R.menu.gov_links_menu, menu)
            true
        }
        else{
            false
        }
    }

    //item selections listener for gov links
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.book_theory_test -> {
                val uri: Uri = Uri.parse("https://www.nidirect.gov.uk/services/book-change-or-cancel-your-theory-test-online")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
                true
            }
            R.id.book_driving_test -> {
                val uri: Uri = Uri.parse("https://www.nidirect.gov.uk/services/book-your-practical-driving-test-online")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    //method to determine what the back button does on this screen
    override fun onSupportNavigateUp() = findNavController(R.id.main_navigation_fragment).navigateUp()


    //method to set up the bottom navigation and choose start destination based off user type
    private fun setUpNavigation(){

        val navHostFragment = main_navigation_fragment as NavHostFragment
        val navController = Navigation.findNavController(this, R.id.main_navigation_fragment)
        val graphInflater = navHostFragment.navController.navInflater
        val navGraph = navController.graph
        val destination = R.id.dummy_frag
        navGraph.startDestination =  destination
        val  dbReference = FirebaseDatabase.getInstance().reference.child("Users")
        dbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild(userId.toString())) {
                    userIsPupil = true
                    val destination = R.id.homeFragment2
                    navGraph.startDestination = destination
                    val navBar = findViewById<BottomNavigationView>(R.id.bottom_tab_navigation)
                    navBar.menu.clear()
                    navBar.inflateMenu(R.menu.bottom_navigation_menu);
                    navController.graph = navGraph
                    NavigationUI.setupWithNavController(bottom_tab_navigation, navController)
                    val appBarConfiguration = AppBarConfiguration(setOf(
                            R.id.homeFragment2, R.id.navigation_booking,
                            R.id.navigation_my_details))
                    setupActionBarWithNavController(navController, appBarConfiguration)
                } else if (!snapshot.hasChild(userId.toString())) {
                    userIsPupil = false
                    val destination = R.id.instructor_home_nav_fragment
                    navGraph.startDestination = destination
                    val navBar = findViewById<BottomNavigationView>(R.id.bottom_tab_navigation)
                    navBar.menu.clear()
                    navBar.inflateMenu(R.menu.instructor_bottom_nav)
                    navController.graph = navGraph
                    val appBarConfiguration = AppBarConfiguration(setOf(
                            R.id.instructor_home_nav_fragment, R.id.instructor_my_pupils))
                    setupActionBarWithNavController(navController, appBarConfiguration)
                    NavigationUI.setupWithNavController(bottom_tab_navigation, navController)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        Log.i("destination" , destination.toString())

    }
}
