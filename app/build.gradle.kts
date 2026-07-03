plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.navigation.safeargs)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.google.services)
    alias(libs.plugins.hilt.android)
}

// Signing configuration from environment variables (CI/CD)
val myKeystorePassword: String? = System.getenv("KEYSTORE_PASSWORD")
val myKeyAlias: String? = System.getenv("KEY_ALIAS")
val myKeyPassword: String? = System.getenv("KEY_PASSWORD") ?: myKeystorePassword
val myKeystoreFile: File = rootProject.file("financontrol.jks")
val canSign = myKeystoreFile.exists() && myKeystorePassword != null && myKeyAlias != null

android {
    compileSdk = 36

    defaultConfig {
        applicationId = "com.locotoDevTeam.financontrol"
        minSdk = 21
        targetSdk = 36
        versionCode = 1
        versionName = "2026.06.11"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (canSign) {
            create("release") {
                storeFile = myKeystoreFile
                storePassword = myKeystorePassword
                keyAlias = myKeyAlias
                keyPassword = myKeyPassword
            }
        }
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        release {
            if (canSign) {
                signingConfig = signingConfigs.getByName("release")
            }
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        // Preserve parameter names for Room annotation processing (KAPT + Kotlin 2.0 compatibility)
        freeCompilerArgs += "-java-parameters"
    }

    namespace = "com.locotoDevTeam.financontrol"
}

// Required by Hilt so KAPT resolves generated types correctly
kapt {
    correctErrorTypes = true
}

dependencies {
    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)

    // UI
    implementation(libs.circleimageview)
    implementation(libs.legacy.support.v4)
    implementation(libs.activity.ktx)
    implementation(libs.fragment.ktx)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.navigation.dynamic.features.fragment)

    // Lifecycle
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)

    // Core
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    // Hilt (Dependency Injection)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Unit test
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.core.testing)
}
