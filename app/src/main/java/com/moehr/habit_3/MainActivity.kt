package com.moehr.habit_3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNavigationView.itemIconTintList = null

        val overviewFragment = Overview()
        val statisticsFragment = Statistics()
        val settingsFragment = Settings()

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
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.main, fragment)
            commit()
        }
}