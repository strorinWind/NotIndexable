package ru.strorin.shareE

import android.app.Application
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKTokenExpiredHandler

class VkApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        VK.addTokenExpiredHandler(tokenTracker)
    }

    private val tokenTracker = object: VKTokenExpiredHandler {
        override fun onTokenExpired() {
            WelcomeActivity.startFrom(this@VkApplication)
        }
    }
}