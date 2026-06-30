# CI/CD with GitHub Actions: FinanControl

## Overview
Continuous Integration and Delivery using GitHub Actions. Builds debug APKs on every push/PR and creates signed GitHub Releases when version tags are pushed.

## Workflows

### 1. Build (`build.yml`)
**Trigger**: Push or PR to `main`/`master`

| Step | Action |
|---|---|
| Checkout | `actions/checkout@v4` |
| JDK 21 | `actions/setup-java@v4` (Temurin 21) |
| Android SDK | `android-actions/setup-android@v3` (platform 36, build-tools 36) |
| Permissions | `chmod +x gradlew` |
| Build | `./gradlew assembleDebug` |
| Test | `./gradlew testDebugUnitTest` |
| Artifact | Uploads `app-debug` APK (wildcard `*.apk`) |

### 2. Release (`release.yml`)
**Trigger**: Pushing a tag matching `v*` (e.g., `v1.0.0`) **or** a branch matching `release/*`

| Step | Action |
|---|---|
| Checkout | `actions/checkout@v4` |
| Version extract | Parses version from tag (`v1.2.3` → `1.2.3`) or branch (`release/1.2.3` → `1.2.3`) |
| Decode keystore | `base64 --decode` from `KEYSTORE_BASE64` secret → `financontrol.jks` |
| JDK 21 | `actions/setup-java@v4` (Temurin 21) |
| Android SDK | `android-actions/setup-android@v3` (platform 36, build-tools 36) |
| Permissions | `chmod +x gradlew` |
| Build signed APK | `./gradlew assembleRelease` with `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD` env vars |
| GitHub Release | `softprops/action-gh-release@v2` with formatted body, APK attached, `draft: false`, `prerelease: false` |

### Release Details
- **Environment**: `PROD` (requires GitHub Environment configured)
- **Permissions**: `contents: write`
- **Release name**: `release-{version}`
- **Release body**: Markdown with tech stack info and install instructions

## Required GitHub Secrets

| Secret | Purpose |
|---|---|
| `KEYSTORE_BASE64` | Base64-encoded JKS keystore file (`financontrol.jks`) |
| `KEYSTORE_PASSWORD` | Keystore password |
| `KEY_ALIAS` | Key alias name |
| `KEY_PASSWORD` | Key password (may be same as keystore password) |

## Signing Config

The `app/build.gradle.kts` reads signing credentials from environment variables:

```kotlin
// Signing configuration from environment variables (CI/CD)
val myKeystorePassword: String? = System.getenv("KEYSTORE_PASSWORD")
val myKeyAlias: String? = System.getenv("KEY_ALIAS")
val myKeyPassword: String? = System.getenv("KEY_PASSWORD") ?: myKeystorePassword
val myKeystoreFile: File = rootProject.file("financontrol.jks")
val canSign = myKeystoreFile.exists() && myKeystorePassword != null && myKeyAlias != null
```

If env vars are not set (local builds), the release build is unsigned. Only the CI/CD pipeline produces signed APKs.

## How to Release

```bash
# Encode the keystore for GitHub Secret (do this once)
base64 -i financontrol.jks | pbcopy

# Create and push a version tag
git tag v1.0.0
git push origin v1.0.0
```

The Release workflow automatically builds a signed APK and publishes it to GitHub Releases.

## Build Environment

| Tool | Version |
|---|---|
| OS | ubuntu-latest |
| JDK | Temurin 21 |
| Android SDK | platform 36, build-tools 36.0.0 |
| AGP | 8.13.2 |
| Kotlin | 2.0.21 |
| Gradle | 8.13 (via wrapper) |
