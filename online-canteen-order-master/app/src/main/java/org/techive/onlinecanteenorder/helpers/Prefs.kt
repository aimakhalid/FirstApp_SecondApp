package org.techive.onlinecanteenorder.helpers

import android.content.Context
import android.content.SharedPreferences

class Prefs (context: Context) {
    val PREFS_FILENAME = "org.techive.onlinecanteenorder"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    val ID = "id"
    val TYPE = "user-type"

    var id: Long?
        get() = prefs.getLong(ID, 0L)
        set(value) = prefs.edit().putLong(ID, value!!).apply()

    var userType: String?
        get() = prefs.getString(TYPE,"")
        set(value) = prefs.edit().putString(TYPE, value!!).apply()



}