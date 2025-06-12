package com.moehr.habit_3

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.moehr.habit_3.ui.overview.Overview
import com.moehr.habit_3.ui.settings.SettingsFragment
import com.moehr.habit_3.ui.statistics.Statistics

class MainActivity : AppCompatActivity() {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNavigationView.itemIconTintList = null

        val overviewFragment = Overview()
        val statisticsFragment = Statistics()
        val settingsFragment = SettingsFragment()

        setCurrentFragment(overviewFragment)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_overview -> setCurrentFragment(overviewFragment)
                R.id.menu_statistics -> setCurrentFragment(statisticsFragment)
                R.id.menu_settings -> setCurrentFragment(settingsFragment)
                else -> return@setOnItemSelectedListener false
            }
            true
        }

        bottomNavigationView.selectedItemId = R.id.menu_overview

        requestNotificationPermission()
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.main, fragment)
            commit()
        }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }
    }
}