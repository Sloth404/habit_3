package com.moehr.habit_3

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.moehr.habit_3.data.preferences.SharedPreferencesManager
import com.moehr.habit_3.ui.overview.Overview
import com.moehr.habit_3.ui.settings.SettingsFragment
import com.moehr.habit_3.ui.statistics.Statistics

class MainActivity : AppCompatActivity() {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply saved theme preference before setting content view
        SharedPreferencesManager.loadTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNavigationView.itemIconTintList = null // Keep original icon colors

        // Instantiate fragments
        val overviewFragment = Overview()
        val statisticsFragment = Statistics()
        val settingsFragment = SettingsFragment()

        // Set default fragment
        if (savedInstanceState == null) {
            val lastScreen = SharedPreferencesManager.loadLastScreen(this)
            val (fragment, menuId) = when (lastScreen) {
                "settings" -> settingsFragment to R.id.menu_settings
                "statistics" -> statisticsFragment to R.id.menu_statistics
                else -> overviewFragment to R.id.menu_overview
            }
            setCurrentFragment(fragment)
            bottomNavigationView.selectedItemId = menuId
        }

        // Set navigation item selection behavior
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_overview -> setCurrentFragment(overviewFragment)
                R.id.menu_statistics -> setCurrentFragment(statisticsFragment)
                R.id.menu_settings -> setCurrentFragment(settingsFragment)
                else -> return@setOnItemSelectedListener false
            }
            true
        }

        // Request POST_NOTIFICATIONS permission if necessary
        requestNotificationPermission()
    }

    /**
     * Replaces the current fragment in the main container.
     */
    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.main, fragment)
            commit()
        }

    /**
     * Requests notification permission for Android 13+ devices.
     */
    private fun requestNotificationPermission() {
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
        }
    }
}