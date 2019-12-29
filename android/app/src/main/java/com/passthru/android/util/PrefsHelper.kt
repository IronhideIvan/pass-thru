package com.passthru.android.util

import android.content.SharedPreferences

object PrefsHelper {
    val KEY_IP_ADDRESS = "ipAddress"
    val KEY_PORT = "portNbr"

    var prefs: SharedPreferences? = null
}