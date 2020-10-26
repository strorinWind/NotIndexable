package ru.strorin.shareE.utils

import android.content.Context
import android.os.Build
import java.util.*

fun getLocale(context: Context?): Locale {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        context?.resources?.configuration?.locales?.get(0)
    } else {
        context?.resources?.configuration?.locale
    } ?: Locale.US
}