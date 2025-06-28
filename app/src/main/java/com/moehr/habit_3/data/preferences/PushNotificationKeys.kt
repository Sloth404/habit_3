package com.moehr.habit_3.data.preferences

/**
 *  Keys for storing push notification times as Strings
 * */
enum class PushNotificationKeys(val id : String) {
    TIME_MORNING("push_morning"),
    TIME_NOON("push_noon"),
    TIME_EVENING("push_evening"),
    TIME_CUSTOM("push_custom"),
}