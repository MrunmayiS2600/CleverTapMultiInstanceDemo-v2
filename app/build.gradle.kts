import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// CleverTap credentials are kept out of source control: they live in
// local.properties (gitignored) and get injected at build time instead.
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) load(file.inputStream())
}

fun cleverTapCred(key: String, placeholder: String): String =
    (localProperties.getProperty(key) ?: System.getenv(key))?.takeIf { it.isNotBlank() } ?: placeholder

android {
    namespace = "com.example.clevertapmultiinstance"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.clevertapmultiinstance"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        manifestPlaceholders["cleverTapProject1AccountId"] =
            cleverTapCred("CLEVERTAP_PROJECT1_ACCOUNT_ID", "YOUR_PROJECT1_ACCOUNT_ID")
        manifestPlaceholders["cleverTapProject1Token"] =
            cleverTapCred("CLEVERTAP_PROJECT1_TOKEN", "YOUR_PROJECT1_ACCOUNT_TOKEN")

        buildConfigField(
            "String", "CLEVERTAP_PROJECT2_ACCOUNT_ID",
            "\"${cleverTapCred("CLEVERTAP_PROJECT2_ACCOUNT_ID", "YOUR_PROJECT2_ACCOUNT_ID")}\""
        )
        buildConfigField(
            "String", "CLEVERTAP_PROJECT2_TOKEN",
            "\"${cleverTapCred("CLEVERTAP_PROJECT2_TOKEN", "YOUR_PROJECT2_ACCOUNT_TOKEN")}\""
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // CleverTap Android SDK (multi-instance support since v3.2.0)
    implementation("com.clevertap.android:clevertap-android-sdk:6.2.1")
    implementation("com.android.installreferrer:installreferrer:2.2")
    implementation("androidx.work:work-runtime:2.9.1")

    // Required for useGoogleAdId/CLEVERTAP_USE_GOOGLE_AD_ID to actually read the GAID;
    // without it CleverTap silently falls back to a random per-install GUID.
    implementation("com.google.android.gms:play-services-ads-identifier:18.3.0")
}
