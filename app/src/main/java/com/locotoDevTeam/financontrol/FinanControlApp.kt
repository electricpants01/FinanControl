package com.locotoDevTeam.financontrol

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point for Hilt dependency injection.
 *
 * [@HiltAndroidApp] triggers Hilt's code generation and creates the application-level
 * dependency container. Every `@AndroidEntryPoint` component (Activities, Fragments,
 * Services) and every `@HiltViewModel` obtains its dependencies from this container.
 *
 * Registered via `android:name=".FinanControlApp"` in AndroidManifest.xml.
 */
@HiltAndroidApp
class FinanControlApp : Application()
