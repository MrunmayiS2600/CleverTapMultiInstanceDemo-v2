package com.example.clevertapmultiinstance

import com.google.firebase.messaging.FirebaseMessagingService

class DemoFcmService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        pushFcmTokenToAllInstances(token)
    }
}

/**
 * Same FCM token handed to all 3 CleverTap instances, so all 3 dashboards register
 * the same device for push - same device, same token, no per-account uniqueness.
 */
fun pushFcmTokenToAllInstances(token: String) {
    CleverTapMultiInstanceApp.project1Instance.pushFcmRegistrationId(token, true)
    CleverTapMultiInstanceApp.project2Instance.pushFcmRegistrationId(token, true)
    CleverTapMultiInstanceApp.project3Instance.pushFcmRegistrationId(token, true)
}
