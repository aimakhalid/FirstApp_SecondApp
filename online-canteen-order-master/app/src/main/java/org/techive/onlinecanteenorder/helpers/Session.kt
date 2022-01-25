package org.techive.onlinecanteenorder.helpers

import android.content.Context

class Session(context: Context) {
    private val TAG = Session::class.java.simpleName

    val prefs = Prefs(context)

    fun setId(id:Long): Session {
        prefs.id = id
        return this
    }

    fun setUserType(type:String): Session {
        prefs.userType = type
        return this
    }

}