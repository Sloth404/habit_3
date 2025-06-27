package com.moehr.habit_3.data.preferences

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

/**
 * Helper class to manage app preferences related to push notification times and theme settings.
 *
 * Uses SharedPreferences to persist user settings.
 */
class SharedPreferencesManager {

    companion object {
        // Name of the SharedPreferences file
        private const val PREFS_NAME = "habit3_prefs"

        // Keys for storing push notification times as Strings
        private const val KEY_PUSH_MORNING = "push_morning"
        private const val KEY_PUSH_NOON = "push_noon"
        private const val KEY_PUSH_EVENING = "push_evening"
        private const val KEY_PUSH_CUSTOM = "push_custom"

        // Keys for theme preferences
        private const val KEY_ICON = "icon_theme"   // Boolean for icon theme (dark/light)
        private const val KEY_APP = "app_theme"     // Boolean for app theme (dark/light)
        private const val KEY_THEME = "theme_mode"  // Int for actual theme mode applied
        private const val KEY_SCREEN = "app_screen" // Screen for reloading the screen after theme change

        // Keys for Settings menu statuses
        private const val KEY_SETTINGS_PUSH = "settings_menu_pn"        // Boolean; true if push notification menu is open
        private const val KEY_SETTINGS_THEME = "settings_menu_theme"    // Boolean; true if theme menu ís open

        /**
         * Saves push notification times and theme preferences to SharedPreferences.
         * Also applies the selected theme immediately.
         *
         * @param context Context to access SharedPreferences and apply theme.
         * @param pushMorning Time string for morning notification.
         * @param pushNoon Time string for noon notification.
         * @param pushEvening Time string for evening notification.
         * @param pushCustom Time string for custom notification.
         * @param icon Boolean indicating icon theme preference (dark or light).
         * @param app Boolean indicating app theme preference (dark or light).
         */
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

            // Save all preferences atomically
            prefs.edit().apply {
                putString(KEY_PUSH_MORNING, pushMorning)
                putString(KEY_PUSH_NOON, pushNoon)
                putString(KEY_PUSH_EVENING, pushEvening)
                putString(KEY_PUSH_CUSTOM, pushCustom)
                putBoolean(KEY_ICON, icon)
                putBoolean(KEY_APP, app)
                apply()
            }

            // Determine night mode based on icon theme preference
            val mode = if (icon) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }

            // Apply theme mode immediately and save it
            setTheme(context, mode)
        }

        /**
         * Loads the saved theme mode from preferences and applies it.
         * Defaults to follow system setting if no preference found.
         *
         * @param context Context to access SharedPreferences.
         */
        fun loadTheme(context: Context) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val mode = prefs.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        /**
         * Applies the given theme mode and persists it in preferences.
         *
         * @param context Context to access SharedPreferences.
         * @param mode The night mode to apply (e.g. MODE_NIGHT_YES, MODE_NIGHT_NO).
         */
        fun setTheme(context: Context, mode: Int) {
            AppCompatDelegate.setDefaultNightMode(mode)
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
                putInt(KEY_THEME, mode)
            }
        }

        fun loadLastScreen(context: Context) : String? {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val screen = prefs.getString(KEY_SCREEN, Screen.OVERVIEW.value)

            // reset last screen, so the user does not always land in settings/statistics screen
            // after reopening the app
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
                putString(KEY_SCREEN, null)
            }
            return screen
        }

        fun setLastScreen(context: Context, screen : Screen) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
                putString(KEY_SCREEN, screen.value)
            }
        }
    }
}
