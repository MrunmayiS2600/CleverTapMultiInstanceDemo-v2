package com.example.clevertapmultiinstance

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.clevertapmultiinstance.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // The FCM token is fetched/registered regardless of this permission - it only
    // gates whether a notification can be shown, not whether the token exists. We
    // still ask for it up front so push notifications actually display later.
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestNotificationPermissionIfNeeded()

        binding.btnLogin.setOnClickListener {
            val phone = binding.etPhone.text?.toString()?.trim().orEmpty()
            if (phone.isEmpty()) {
                binding.tilPhone.error = "Enter a phone number"
                return@setOnClickListener
            }
            binding.tilPhone.error = null
            loginToAllDashboards(phone)
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return // granted at install below API 33
        val granted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun loginToAllDashboards(phone: String) {
        // Same Identity (the phone number) goes to all three instances, so all three
        // dashboards resolve to the same user. Combined with Google Ad ID being enabled
        // on every instance (device GUID derived from the shared GAID), both the Identity
        // and the underlying CleverTap ID line up across all three accounts.
        val profile = HashMap<String, Any>()
        profile["Identity"] = phone
        profile["Phone"] = phone

        val instances = listOf(
            CleverTapMultiInstanceApp.project1Instance,
            CleverTapMultiInstanceApp.project2Instance,
            CleverTapMultiInstanceApp.project3Instance
        )
        instances.forEach { instance ->
            instance.onUserLogin(profile)
            instance.pushEvent("Logged In")
        }

        binding.tvStatus.text = "Identity sent to all 3 dashboards: $phone"
    }
}
