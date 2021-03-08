package com.ghuyfel.weather.ui.activities

import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavDirections
import androidx.navigation.Navigator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.ghuyfel.weather.R
import com.ghuyfel.weather.databinding.ActivityMainBinding
import com.ghuyfel.weather.utils.Constants
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }

        binding.toolbar.setBackgroundColor(Color.TRANSPARENT)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val toggle = ActionBarDrawerToggle(
            this,
            binding.root,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        binding.root.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration.Builder()
            .setOpenableLayout(binding.root).build()

        appBarConfiguration.topLevelDestinations.add(R.id.nav_home)

        NavigationUI.setupWithNavController(binding.navView, navController)
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(binding.toolbar, navController, binding.root)

        binding.navView.setNavigationItemSelectedListener { dest ->
            if (dest.itemId != navController.currentDestination?.id) {
                NavigationUI.onNavDestinationSelected(dest, navController)
                binding.root.closeDrawers()
            }

            true
        }

        Places.initialize(applicationContext, Constants.API_KEY_PLACES_API)
        setContentView(binding.root)
    }

    override fun onBackPressed() {

        if (binding.root.isOpen)
            binding.root.close()
        else
            super.onBackPressed()
    }
}