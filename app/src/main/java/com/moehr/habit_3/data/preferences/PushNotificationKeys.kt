package com.moehr.habit_3.data.preferences

/**
 *  Keys for storing push notification times as Strings
 * */
enum class PushNotificationKeys(val id : String, val defaultTime : String) {
    TIME_MORNING("push_morning", "09:00"),
    TIME_NOON("push_noon", "13:00"),
    TIME_EVENING("push_evening", "18:00"),
    TIME_CUSTOM("push_custom", "21:00"),
}