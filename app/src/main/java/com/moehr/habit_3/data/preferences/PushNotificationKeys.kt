package com.moehr.habit_3.data.preferences

import android.content.Context
import com.moehr.habit_3.R

/**
 *  Keys for notifications times. Furthermore each key stores the notification id used in notification
 *  management and the according string id for reading the default times from the string resources.
 * */
enum class PushNotificationKeys(val id : String, private val defaultTimeId : Int) {
    TIME_MORNING("push_morning", R.string.settings_morning_time),
    TIME_NOON("push_noon", R.string.settings_noon_time),
    TIME_EVENING("push_evening", R.string.settings_evening_time),
    TIME_CUSTOM("push_custom", R.string.settings_custom_time);

    fun getDefaultTime(context : Context) : String {
        return context.getString(defaultTimeId)
    }
}