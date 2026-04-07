# DBMS Tool (Master Branch)

This README is for the `master` branch of this repository:
https://github.com/Arhanpg/DBMS-Backend/tree/master

The `master` branch contains an **Android client app** (Jetpack Compose) that connects to the DBMS backend API.

---

## Overview

`DBMS Tool` is a mobile interface for the University Accommodation DBMS project.  
It lets users:

- Browse available database tables
- View table data in a scrollable data grid
- Run 14 preset project queries
- Execute custom SQL (`SELECT`-only, validated by backend)

The app calls the deployed backend API:

- Base URL (current in code): `https://dbms-backend-theta.vercel.app/`

---

## Tech Stack

- Kotlin
- Android Studio + Gradle (KTS)
- Jetpack Compose (Material 3)
- Navigation Compose
- Retrofit 2 + Gson Converter
- Kotlin Coroutines
- ViewModel + StateFlow

---

## Project Structure (master branch)

```text
.
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/dbmstool/
│       │   ├── MainActivity.kt
│       │   ├── data/
│       │   │   ├── api/
│       │   │   │   ├── ApiService.kt
│       │   │   │   └── RetrofitClient.kt
│       │   │   └── model/Models.kt
│       │   ├── navigation/AppNavigation.kt
│       │   ├── repository/AccommodationRepository.kt
│       │   ├── ui/
│       │   │   ├── components/
│       │   │   │   ├── DataTable.kt
│       │   │   │   └── QueryResultCard.kt
│       │   │   ├── screens/
│       │   │   │   ├── HomeScreen.kt
│       │   │   │   ├── TablesScreen.kt
│       │   │   │   ├── QueriesScreen.kt
│       │   │   │   └── CustomQueryScreen.kt
│       │   │   └── theme/
│       │   └── viewmodel/MainViewModel.kt
│       └── res/
├── gradle/libs.versions.toml
├── build.gradle.kts
├── settings.gradle.kts
└── gradlew
```

---

## App Flow

- `MainActivity` sets Compose content and starts app navigation.
- Bottom navigation has 4 screens:
  - **Home**: static overview of DB tables
  - **Tables**: fetches table names and table data
  - **Queries**: lists preset queries and executes selected query
  - **Custom**: sends user SQL query to backend (`POST /custom/run`)

State management is handled in `MainViewModel` with `StateFlow` + `UiState` (Idle/Loading/Success/Error).

---

## Backend Integration

Retrofit interface (`ApiService`) maps to backend endpoints:

- `GET /tables`
- `GET /tables/{name}`
- `GET /queries`
- `POST /queries/{id}/run`
- `POST /custom/run`

Important request model:

- `QueryRunRequest(param_values: List<String>)`  
  This matches the backend’s expected request body key (`param_values`).

---

## Requirements

- Android Studio (latest stable recommended)
- JDK 11 (project compiles with Java 11 target)
- Android SDK (compile/target SDK configured as 36)
- Internet access from emulator/device

---

## How to Run (master branch)

1. Open the `master` branch project in Android Studio.
2. Let Gradle sync and download dependencies.
3. Select an emulator/device.
4. Run the app (`Run > Run 'app'`).

Or CLI:

```bash
./gradlew assembleDebug
```

Install/run from Android Studio or with adb if needed.

---

## Configuration Notes

### API Base URL

`RetrofitClient.kt` currently uses:

```kotlin
private const val BASE_URL = "https://dbms-backend-theta.vercel.app/"
```

If you want to point to local backend:

- Android emulator localhost uses `10.0.2.2`
- Example: `http://10.0.2.2:8000/`

`AndroidManifest.xml` already includes:

- `INTERNET` permission
- `android:usesCleartextTraffic="true"` for HTTP local testing

---

## Build and Test

Build debug APK:

```bash
./gradlew assembleDebug
```

Unit tests:

```bash
./gradlew test
```

Instrumented tests (device/emulator required):

```bash
./gradlew connectedAndroidTest
```

---

## Branch Notes

- `master` branch: Android client app (this README)
- `main` branch: FastAPI backend service

If you need end-to-end functionality, keep backend deployed/running and make sure `BASE_URL` points to that backend.
