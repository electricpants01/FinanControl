# Firebase Integration: FinanControl

## Firebase Services in Use

| Service | Version | Gradle Plugin | Purpose |
|---|---|---|---|
| **Analytics** | 20.1.0 | (via BOM) | App usage tracking |
| **Authentication** | 21.0.1 | (via BOM) | Firebase Auth (user login) |
| **Crashlytics** | 18.2.8 | `com.google.firebase.crashlytics` | Crash reporting |
| **Cloud Messaging (FCM)** | 23.0.0 | `com.google.gms.google-services` | Push notifications |

## Firebase BOM

The project uses Firebase Bill of Materials (BOM) for version management:

```kotlin
implementation platform('com.google.firebase:firebase-bom:29.1.0')
```

## Push Notification Service

**Class**: `PushNotificationService` at `com.locotoDevTeam.financontrol.data.FBMessaging.PushNotificationService`

- Extends `FirebaseMessagingService`
- Declared in `AndroidManifest.xml` with `INSTANCE_ID_EVENT` intent filter
- Handles Firebase Cloud Messaging events

### AndroidManifest Registration
```xml
<service
    android:name=".data.FBMessaging.PushNotificationService"
    android:exported="false">
    <intent-filter>
        <action android:name="com.google.firebase.INSTANCE_ID_EVENT" />
    </intent-filter>
</service>
```

### Default Notification Icon
```xml
<meta-data
    android:name="com.google.firebase.messaging.default_notification_icon"
    android:resource="@drawable/splash_icon" />
```

## Configuration Notes

- Firebase configuration is handled by the `google-services.json` plugin (standard Firebase setup)
- Crashlytics uses the `com.google.firebase.crashlytics` Gradle plugin
- Analytics is initialized automatically via Firebase plugin
- Auth is available via `firebase-auth-ktx` dependency for potential future use or existing authentication flow
