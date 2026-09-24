package com.example.clevertapmultiinstance

import android.app.Application
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.CleverTapInstanceConfig
import com.google.firebase.messaging.FirebaseMessaging

class CleverTapMultiInstanceApp : Application() {

    companion object {
        // Project 1 = default instance, credentials come from AndroidManifest.xml meta-data.
        lateinit var project1Instance: CleverTapAPI

        // Project 2 = additional instance, credentials configured below.
        lateinit var project2Instance: CleverTapAPI

        // Project 3 = second additional instance, credentials configured below.
        lateinit var project3Instance: CleverTapAPI
    }

    override fun onCreate() {
        super.onCreate()

        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.DEBUG)

        // Project 1 reads CLEVERTAP_USE_GOOGLE_AD_ID=1 from AndroidManifest.xml, so its
        // device GUID is derived from the device's Google Ad ID instead of a random UUID.
        project1Instance = CleverTapAPI.getDefaultInstance(applicationContext)!!

        val project2Config = CleverTapInstanceConfig.createInstance(
            applicationContext,
            BuildConfig.CLEVERTAP_PROJECT2_ACCOUNT_ID,
            BuildConfig.CLEVERTAP_PROJECT2_TOKEN
        )
        project2Config.setDebugLevel(CleverTapAPI.LogLevel.DEBUG)
        // Mirrors Project 1's CLEVERTAP_USE_GOOGLE_AD_ID manifest flag: Project 1 is
        // configured via manifest meta-data, Project 2 via this config object instead.
        // Since both instances read the same device's Google Ad ID, they end up with the
        // same GAID-derived CleverTap ID (device GUID) - no custom ID needed.
        project2Config.useGoogleAdId(true)
        project2Instance = CleverTapAPI.instanceWithConfig(applicationContext, project2Config)!!

        val project3Config = CleverTapInstanceConfig.createInstance(
            applicationContext,
            BuildConfig.CLEVERTAP_PROJECT3_ACCOUNT_ID,
            BuildConfig.CLEVERTAP_PROJECT3_TOKEN
        )
        project3Config.setDebugLevel(CleverTapAPI.LogLevel.DEBUG)
        project3Config.useGoogleAdId(true)
        project3Instance = CleverTapAPI.instanceWithConfig(applicationContext, project3Config)!!

        // "App Launched" is a reserved/internal CleverTap event name and gets
        // silently rejected (wzrk_error 513) if pushed as a custom event.
        project1Instance.pushEvent("Demo App Opened")
        project2Instance.pushEvent("Demo App Opened")
        project3Instance.pushEvent("Demo App Opened")

        // onNewToken (DemoFcmService) only fires when the token actually changes, so
        // fetch the current one here too and push it to all 3 instances on every launch.
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            val token = task.result ?: return@addOnCompleteListener
            pushFcmTokenToAllInstances(token)
        }
    }
}
