package ru.strorin.shareE

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope

class WelcomeActivity: Activity() {

    private val permissions = arrayListOf(VKScope.WALL, VKScope.PHOTOS)

    private val vkAuthCallback = object: VKAuthCallback {

        override fun onLogin(token: VKAccessToken) {
            gotoMainActivity()
        }

        override fun onLoginFailed(errorCode: Int) { }
    }

    companion object {
        fun startFrom(context: Context) {
            val intent = Intent(context, WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (VK.isLoggedIn()) {
            gotoMainActivity()
            return
        }
        setContentView(R.layout.activity_welcome)

        val loginBtn = findViewById<Button>(R.id.loginBtn)
        loginBtn.setOnClickListener {
            VK.login(this, permissions)
        }
    }

    override fun onStart() {
        super.onStart()
        VK.login(this, permissions)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!VK.onActivityResult(requestCode, resultCode, data, vkAuthCallback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun gotoMainActivity() {
        MainActivity.startFrom(this@WelcomeActivity)
        finish()
    }
}

