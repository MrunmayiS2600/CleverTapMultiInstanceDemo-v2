package com.example.clevertapmultiinstance

import android.app.Application
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.CleverTapInstanceConfig

class CleverTapMultiInstanceApp : Application() {

    companion object {
        // Project 1 = default instance, credentials come from AndroidManifest.xml meta-data.
        lateinit var project1Instance: CleverTapAPI

        // Project 2 = additional instance, credentials configured below.
        lateinit var project2Instance: CleverTapAPI
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

        // "App Launched" is a reserved/internal CleverTap event name and gets
        // silently rejected (wzrk_error 513) if pushed as a custom event.
        project1Instance.pushEvent("Demo App Opened")
        project2Instance.pushEvent("Demo App Opened")
    }
}
