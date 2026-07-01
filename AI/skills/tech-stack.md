# Tech Stack: FinanControl

## Language & Platform

| Technology | Version / Details |
|---|---|
| **Kotlin** | 2.0.21 (JVM target 1.8) |
| **Java** | Some legacy chart classes in Java (FancyChart, ChartData, etc.) |
| **Android** | compileSdk 36, targetSdk 36, minSdk 21 |
| **JDK** | 21 (Temurin on CI, Android Studio bundled locally) |

## Build System

| Tool | Details |
|---|---|
| **Gradle** | 8.13 (via wrapper) |
| **DSL** | Kotlin DSL (`build.gradle.kts`, `settings.gradle.kts`) |
| **Version Catalog** | `gradle/libs.versions.toml` |
| **AGP** | 8.13.2 |
| **Plugins** | `com.android.application`, `org.jetbrains.kotlin.android`, `org.jetbrains.kotlin.kapt`, `androidx.navigation.safeargs.kotlin`, `com.google.firebase.crashlytics`, `com.google.gms.google-services` |

## Core Libraries

| Library | Version | Purpose |
|---|---|---|
| **Jetpack Navigation** | 2.8.7 | Fragment navigation with Safe Args |
| **Room** | 2.4.1 | Local SQLite database (kapt) |
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

## Test Libraries

| Library | Version | Purpose |
|---|---|---|
| **JUnit** | 4.13.2 | Unit test framework |
| **MockK** | 1.13.13 | Kotlin mocking library (supports suspend functions via `coEvery`/`coVerify`) |
| **Kotlinx Coroutines Test** | 1.8.1 | `runTest`, `StandardTestDispatcher`, `advanceUntilIdle()` |
| **Arch Core Testing** | 2.2.0 | `InstantTaskExecutorRule` for LiveData synchronous testing |

> **Note**: MockK is used instead of Mockito because MockK natively supports Kotlin `suspend` functions, `object`/companion mocking, and relaxed mocks — all needed for testing coroutine-heavy ViewModels and Repositories.

## Repository Configuration

- `google()`
- `mavenCentral()`
- `gradlePluginPortal()` (for plugin resolution)
- ❌ `jcenter()` removed (deprecated)

## CI/CD

| Tool | Details |
|---|---|
| **Platform** | GitHub Actions |
| **Workflows** | `build.yml` (push/PR → debug APK + tests), `release.yml` (tag `v*` → signed release APK → GitHub Release) |
| **Signing** | Env-var-based (`KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`), conditional (only on CI) |
| **Keystore** | `financontrol.jks` at project root |
