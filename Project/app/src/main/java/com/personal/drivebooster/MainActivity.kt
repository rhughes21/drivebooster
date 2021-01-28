package com.personal.drivebooster

import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpNavigation()
        if (supportActionBar != null) {
            val actionBar: ActionBar? = supportActionBar
            actionBar?.setDisplayHomeAsUpEnabled(false)
        }

    }
    //method to determine what the back button does on this screen
    override fun onSupportNavigateUp() = findNavController(R.id.main_navigation_fragment).navigateUp()

    val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.homeFragment2, R.id.navigation_booking,
            R.id.navigation_my_details))

    //method to set up the bottom navigation
    private fun setUpNavigation(){
        val navController = Navigation.findNavController(this, R.id.main_navigation_fragment)
        setupActionBarWithNavController(navController, appBarConfiguration)
        NavigationUI.setupWithNavController(bottom_tab_navigation, navController)
    }
}