package com.moehr.habit_3.data.preferences

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import kotlin.collections.Map

/**
 * Helper class to manage app preferences related to push notification times and theme settings.
 *
 * Uses SharedPreferences to persist user settings.
 */
class SharedPreferencesManager {

    companion object {
        // Name of the SharedPreferences file
        private const val PREFS_NAME = "habit3_prefs"

        // Keys for theme preferences
        private const val KEY_ICON = "icon_theme"   // Boolean for icon theme (dark/light)
        private const val KEY_APP = "app_theme"     // Boolean for app theme (dark/light)
        private const val KEY_THEME = "theme_mode"  // Int for actual theme mode applied

        /**
         * Saves push notification times and theme preferences to SharedPreferences.
         * Also applies the selected theme immediately.
         *
         * @param context Context to access SharedPreferences and apply theme.
         * @param pushMorning Time string for morning notification.
         * @param pushNoon Time string for noon notification.
         * @param pushEvening Time string for evening notification.
         * @param pushCustom Time string for custom notification.
         * @param icon [future feature] Boolean indicating icon theme preference (dark or light).
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
                putString(PushNotificationKeys.TIME_MORNING.id, pushMorning)
                putString(PushNotificationKeys.TIME_NOON.id, pushNoon)
                putString(PushNotificationKeys.TIME_EVENING.id, pushEvening)
                putString(PushNotificationKeys.TIME_CUSTOM.id, pushCustom)
                putBoolean(KEY_ICON, icon)
                putBoolean(KEY_APP, app)
                apply()
            }
        }

        /**
         * Retrieves the notification time values saved in the shared preferences
         *
         * @param context Context to access SharedPreferences and apply theme.
         * @return [Map] of the notification id (key) and the saved time (value)
         * */
        fun loadPushSettings(context: Context) : Map<String, String> {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val pushMorning = prefs.getString(PushNotificationKeys.TIME_MORNING.id, "")
            val pushNoon = prefs.getString(PushNotificationKeys.TIME_NOON.id, "")
            val pushEvening = prefs.getString(PushNotificationKeys.TIME_EVENING.id, "")
            val pushCustom = prefs.getString(PushNotificationKeys.TIME_CUSTOM.id, "")

            return buildMap {
                put(PushNotificationKeys.TIME_MORNING.id, pushMorning ?: "")
                put(PushNotificationKeys.TIME_NOON.id, pushNoon ?: "")
                put(PushNotificationKeys.TIME_EVENING.id, pushEvening ?: "")
                put(PushNotificationKeys.TIME_CUSTOM.id, pushCustom ?: "")
            }
        }

        /**
         * Loads the time (as `hh:mm`) for a specific notification id
         *
         * @param context Context to access SharedPreferences and apply theme.
         * @param notificationId id of the notification time
         * */
        fun loadSpecificPushSetting(context: Context, notificationId: String) : String {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return when (notificationId) {
                PushNotificationKeys.TIME_MORNING.id -> prefs.getString(notificationId, "") ?: ""
                PushNotificationKeys.TIME_NOON.id -> prefs.getString(notificationId, "") ?: ""
                PushNotificationKeys.TIME_EVENING.id -> prefs.getString(notificationId, "") ?: ""
                PushNotificationKeys.TIME_CUSTOM.id -> prefs.getString(notificationId, "") ?: ""
                else -> ""
            }
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
    }
}
