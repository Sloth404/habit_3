package com.moehr.habit_3.data.preferences

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

class SharedPreferencesManager {
    companion object {
        private const val PREFS_NAME = "habit3_prefs"
        private const val KEY_PUSH_MORNING = "push_morning"
        private const val KEY_PUSH_NOON = "push_noon"
        private const val KEY_PUSH_EVENING = "push_evening"
        private const val KEY_PUSH_CUSTOM = "push_custom"
        private const val KEY_ICON = "icon_theme"
        private const val KEY_APP = "app_theme"
        private const val KEY_THEME = "theme_mode"

        fun saveSettings(
            context: Context,
            pushMorning: String,
            pushNoon: String,
            pushEvening: String,
            pushCustom: String,
            icon: Boolean,
            app: Boolean
        ) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().apply {
                putString(KEY_PUSH_MORNING, pushMorning)
                putString(KEY_PUSH_NOON, pushNoon)
                putString(KEY_PUSH_EVENING, pushEvening)
                putString(KEY_PUSH_CUSTOM, pushCustom)
                putBoolean(KEY_ICON, icon)
                putBoolean(KEY_APP, app)
                apply()
            }

            val isDarkMode = icon
            val mode = if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            setTheme(context, mode)
        }

        fun loadTheme(context: Context) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val mode = prefs.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        fun setTheme(context: Context, mode: Int) {
            AppCompatDelegate.setDefaultNightMode(mode)
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
                putInt(KEY_THEME, mode)
            }
        }
    }

}
