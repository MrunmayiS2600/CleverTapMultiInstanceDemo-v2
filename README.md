# CleverTap Multi-Instance Demo

Android (Kotlin) app demonstrating CleverTap's multi-instance SDK support: one app
sending the same user data to **three** separate CleverTap accounts at once.

## The three instances

- **Project 1** — the default instance, configured via `AndroidManifest.xml` meta-data
  (`CLEVERTAP_ACCOUNT_ID` / `CLEVERTAP_TOKEN`, resolved from `local.properties`).
- **Project 2** and **Project 3** — additional instances created in code
  ([CleverTapMultiInstanceApp.kt](app/src/main/java/com/example/clevertapmultiinstance/CleverTapMultiInstanceApp.kt)),
  credentials resolved from `local.properties` via generated `BuildConfig` fields.

All three enable `useGoogleAdId`/`CLEVERTAP_USE_GOOGLE_AD_ID`, so each instance derives its
CleverTap device ID from the device's Google Ad ID (GAID) instead of a random UUID. Since
it's the same physical device, all three end up with the same underlying CleverTap ID -
no custom-ID workaround needed.

## Login behavior

Tapping login in `MainActivity.kt` sends the **same** profile (`Identity` and `Phone` = the
phone number typed in) to all three instances via `onUserLogin`, followed by a `Logged In`
event on each. So for a given login: same Identity, same GAID-derived CleverTap ID, and
(see below) the same FCM push token, across all three dashboards.

## Push notifications (FCM)

`DemoFcmService` and the startup code in `CleverTapMultiInstanceApp.kt` fetch the device's
Firebase Cloud Messaging token once and register it (`pushFcmRegistrationId`) on all three
instances - same device, same token, sent to all three. `MainActivity.kt` requests the
`POST_NOTIFICATIONS` runtime permission on first launch (Android 13+) so notifications can
actually be shown; the token itself is fetched/registered regardless of that permission.

This requires a real Firebase project - see setup below.

## Setup

1. Open this folder in Android Studio (or run via Gradle/CLI) - it generates the wrapper
   and syncs automatically.
2. Copy `local.properties.example` to `local.properties` and fill in real Account
   ID/Token pairs for three CleverTap accounts (`CLEVERTAP_PROJECT1_*`, `_PROJECT2_*`,
   `_PROJECT3_*`).
3. Create a Firebase project with an Android app registered under package name
   `com.example.clevertapmultiinstance`, download its `google-services.json`, and place it
   at `app/google-services.json`. This file is gitignored - **never commit it**, since it
   embeds a live API key. Without it the build fails (the Google Services Gradle plugin
   requires the file to exist).
4. Run on a device/emulator, allow the notification permission prompt, enter a phone
   number, tap login.
5. Check all three CleverTap dashboards (Profiles / Events) for the same `Identity` and
   the same device-registered push token.

## Repository

`github.com/MrunmayiS2600/CleverTapMultiInstanceDemo-v2`
