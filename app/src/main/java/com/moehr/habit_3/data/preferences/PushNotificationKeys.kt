package com.moehr.habit_3.data.preferences

/**
 *  Keys for storing push notification times as Strings
 * */
enum class PushNotificationKeys(val id : String) {
    MORNING("push_morning"),
    NOON("push_noon"),
    EVENING("push_evening"),
    CUSTOM("push_custom")
}