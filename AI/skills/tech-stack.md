# Tech Stack: FinanControl

## Language & Platform

| Technology | Version / Details |
|---|---|
| **Kotlin** | JVM target 1.8 |
| **Java** | Some legacy chart classes in Java (FancyChart, ChartData, etc.) |
| **Android** | compileSdk 32, targetSdk 32, minSdk 21 |

## Build System

| Tool | Details |
|---|---|
| **Gradle** | Kotlin DSL (`build.gradle` files) |
| **Plugins** | `com.android.application`, `kotlin-android`, `kotlin-kapt`, `androidx.navigation.safeargs.kotlin`, `com.google.firebase.crashlytics`, `com.google.gms.google-services` |

## Core Libraries

| Library | Version | Purpose |
|---|---|---|
| **Jetpack Navigation** | 2.4.1 | Fragment navigation with Safe Args |
| **Room** | 2.4.1 | Local SQLite database |
| **Lifecycle (ViewModel + LiveData)** | 2.5.0-alpha02 | MVVM architecture |
| **ViewBinding** | Built-in (enabled) | Type-safe view access |
| **Material Design** | 1.5.0 | UI components (MaterialAlertDialogBuilder, themes) |
| **ConstraintLayout** | 2.1.3 | Layout |
| **Core KTX** | 1.7.0 | Kotlin extensions for Android |
| **AppCompat** | 1.4.1 | Backward-compatible activity/fragment support |
| **Fragment KTX** | 1.4.1 | Fragment Kotlin extensions |
| **Activity KTX** | 1.4.0 | Activity Kotlin extensions |
| **Legacy Support v4** | 1.0.0 | Legacy support |

## Firebase

| Firebase Service | Version | Purpose |
|---|---|---|
| **Firebase BOM** | 29.1.0 | Firebase dependency management |
| **Analytics** | 20.1.0 | Usage analytics |
| **Auth KTX** | 21.0.1 | Firebase Authentication |
| **Crashlytics** | 18.2.8 | Crash reporting |
| **Cloud Messaging** | 23.0.0 | Push notifications |

## UI / Visual

| Library | Version | Purpose |
|---|---|---|
| **CircleImageView** | 3.1.0 | Circular image views |
| **FancyChart** (custom) | — | Custom charting for income/expense visualization |

## Key Plugins (Gradle)

```kotlin
plugins {
    id 'com.android.application'
    id 'kotlin-android'
    id 'kotlin-kapt'                         // For Room annotation processing
    id 'androidx.navigation.safeargs.kotlin' // Type-safe navigation arguments
    id 'com.google.firebase.crashlytics'     // Crashlytics
    id 'com.google.gms.google-services'      // Google Services
}
```

## Repository Configuration

- `google()`
- `mavenCentral()`
- `jcenter()` (⚠️ deprecated/shutting down)
