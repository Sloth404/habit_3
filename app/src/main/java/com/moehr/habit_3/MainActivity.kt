package com.moehr.habit_3

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.os.Bundle
import android.content.Intent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.moehr.habit_3.data.preferences.SharedPreferencesManager
import com.moehr.habit_3.data.viewmodel.HabitViewModel
import com.moehr.habit_3.notification.HabitActionReceiver
import com.moehr.habit_3.notification.NotificationAlarmManager
import com.moehr.habit_3.notification.NotificationHelper
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

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        // Initialize BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNavigationView.itemIconTintList = null // Keep original icon colors

        // Instantiate fragments
        val overviewFragment = Overview()
        val statisticsFragment = Statistics()
        val settingsFragment = SettingsFragment()

        // Set default fragment
        if (savedInstanceState == null) {
            setCurrentFragment(overviewFragment)
            bottomNavigationView.selectedItemId = R.id.menu_overview
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

        // Request SCHEDULE_EXACT_ALARM permission if necessary
        requestExactAlarmPermission()

        // create the notification chanel
        NotificationHelper().createNotificationChannel(this)
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

    private fun requestExactAlarmPermission() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (!alarmManager.canScheduleExactAlarms()) {
            val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            startActivity(intent)
        }
    }
}