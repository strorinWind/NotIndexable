package ru.strorin.shareE.permission

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi


object PermissionUtils {

    fun setShouldShowStatus(context: Context, permission: String) {
        val genPrefs = context.getSharedPreferences("GENERIC_PREFERENCES", Context.MODE_PRIVATE)
        val editor = genPrefs.edit()
        editor.putBoolean(permission, true)
        editor.apply()
    }

    fun getRationaleDisplayStatus(context: Context, permission: String): Boolean {
        val genPrefs = context.getSharedPreferences("GENERIC_PREFERENCES", Context.MODE_PRIVATE)
        return genPrefs.getBoolean(permission, false)
    }
}