package de.kai_morich.simple_bluetooth_le_terminal

internal object Constants {
    // values have to be globally unique
    val INTENT_ACTION_DISCONNECT: String = BuildConfig.APPLICATION_ID + ".Disconnect"
    val NOTIFICATION_CHANNEL: String = BuildConfig.APPLICATION_ID + ".Channel"
    val INTENT_CLASS_MAIN_ACTIVITY: String = BuildConfig.APPLICATION_ID + ".MainActivity"

    // values have to be unique within each app
    const val NOTIFY_MANAGER_START_FOREGROUND_SERVICE = 1001
}