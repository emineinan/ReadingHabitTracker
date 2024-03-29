package com.hms.readinghabittracker.utils

object Constant {
    const val KEY_AVAILABILITY_CALENDAR_SHOULD_REFRESH = 1002

    val filters: ArrayList<String> = arrayListOf(
        "No filter",
        "Black-and-white",
        "Brown tone",
        "Lazy",
        "Freesia",
        "Fuji",
        "Peach pink",
        "Sea salt",
        "Mint",
        "Reed",
        "Vintage",
        "Marshmallow",
        "Moss",
        "Sunlight",
        "Time",
        "Haze blue",
        "Sunflower",
        "Hard",
        "Bronze yellow",
        "Monochromatic tone",
        "Yellow-green tone",
        "Yellow tone",
        "Green tone",
        "Cyan tone",
        "Violet tone"
    )

    const val API_KEY = "API_KEY_VALUE"
    const val PROJECT_ID = "PROJECT_ID"
    const val APP_ID = "APP_ID"
    const val CLIENT_SECRET = "CLIENT_SECRET"
    const val CLIENT_ID = "CLIENT_ID"

    // Filter Strings
    const val intensity = "intensity"
    const val filterType = "filterType"
    const val compressRate = "compressRate"
    const val requestId = "requestId"
    const val taskJson = "taskJson"
    const val authJson = "authJson"

    // Database
    const val DATABASE_NAME = "goals_database"

    // Receiver
    const val TAG_GOAL_NAME = "GOAL_NAME"
    const val TAG_GOAL_ID = "GOAL_ID"
    const val NOTIFICATIONS_CHANNEL_ID = "notifications_id"
    const val NOTIFICATIONS_CHANNEL_NAME = "Goals"
    const val INITIAL_FRAGMENT = "INITIAL_FRAGMENT"

    // Map
    const val MAP_KEY = "DAEDAAckTAdZ/aWT5I35ayZZQXYPLugtG7hgfF3wAVnK6s9XutrbTnts4KhSi3NVxIyofRq5EvzlCUR0AFGoJRoa6LtFEOB5TuJ2Vg=="
}