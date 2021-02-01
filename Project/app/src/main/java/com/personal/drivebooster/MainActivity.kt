package com.personal.drivebooster

import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.home_fragment.*


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

    //method to set up the bottom navigation
    private fun setUpNavigation(){
        val navController = Navigation.findNavController(this, R.id.main_navigation_fragment)
        setupActionBarWithNavController(navController)
        NavigationUI.setupWithNavController(bottom_tab_navigation, navController)
    }
}