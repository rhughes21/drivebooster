package com.personal.drivebooster

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpNavigation()
    }

    override fun onSupportNavigateUp() = findNavController(R.id.main_navigation_fragment).navigateUp()


    private fun setUpNavigation(){
        val navController = Navigation.findNavController(this, R.id.main_navigation_fragment)
        setupActionBarWithNavController(navController)
        NavigationUI.setupWithNavController(bottom_tab_navigation, navController)
    }
}