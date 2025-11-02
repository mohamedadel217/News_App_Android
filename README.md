# NewsApp

Top headlines app built with **Kotlin**, **Jetpack Compose**, and a modular Clean Architecture.  
Supports **product flavors** (e.g., **BBC News**, **CNN**), **biometric gate** on startup, and **unit and UI tests**.

---

## ✨ Features

- **Headlines list**
  - Provider name shown as **screen title** (from flavor).
  - Headlines **sorted by date (desc)**.
  - Each item shows **title** and **image** (Coil with caching).
  - Infinite-scroll–friendly list.

- **Details screen**
  - Tap a headline to open details with **image, title, description, content** (when available).

- **Biometric (optional)**
  - If device supports & configured, prompt for **fingerprint** on app open; otherwise proceed normally.

- **Flavors**
  - Build per source (e.g., `bbc`, `cnn`). Headlines reflect the **active flavor**.

---

## 🧱 Modules
app // Application entry, NavigationGraph, theme, flavors

base // (shared base)

common // Shared utils (PagingModel, date parsing/formatting, BiometricHelper, etc.)

domain // Entities, repository interfaces, use cases

data // Repository implementations, mapping, orchestration

remote // Retrofit, OkHttp, Moshi, API service + network models

local //  Room DB, DAOs

feature // UI (Compose), ViewModels, contracts, mappers
---

## 🛠 Tech Stack

- **Kotlin**, **Coroutines/Flow**
- **Jetpack Compose** (Material 3), **Navigation Compose**
- **Hilt** for DI
- **Retrofit** + **OkHttp** + **Moshi**
- **Coil** for images
- **Biometric**: `androidx.biometric:biometric`
- **Testing**: JUnit4, Truth, Turbine, MockK, Compose UI Test, Robolectric 

---

## 📦 Product Flavors

Example flavors:

- `bbc` → `BuildConfig.NEWS_SOURCE = "bbc-news"` → title “BBC News”
- `cnn` → `BuildConfig.NEWS_SOURCE = "cnn"` → title “CNN”

Select build variant in Android Studio (e.g., `bbcDebug`, `cnnDebug`).

---


   
