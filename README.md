# 🎭 MyEmotions

**MyEmotions** is a modern, privacy-focused Android application designed to help you track, understand, and share your emotional well-being. Using advanced emotion detection and social connectivity, it brings a new dimension to mental health awareness and social interaction.

---

## ✨ Features

### 📸 Smart Emotion Capture
*   **AI-Powered Detection**: Use your camera to capture and analyze your current emotion in real-time using CameraX and ML models.
*   **Manual Logging**: Quickly log your mood with personalized notes when you don't want to use the camera.

### 📊 Insights & Analytics
*   **Mood Distribution**: Visualize your emotional trends over the last 30 days with beautiful, interactive charts.
*   **History Tracking**: Keep a detailed log of your emotional journey and see how your feelings evolve over time.
*   **Personalized Recommendations**: Receive tailored suggestions and activities based on your current emotional state to help you feel better or maintain your positivity.

### 🌌 The "Space" (Social)
*   **Friend Feed**: Stay connected with your inner circle. See how your friends are feeling and support them when they need it.
*   **Space Map**: A unique way to see your friends' moods geographically. (Privacy-first: precise locations are never shared without consent).
*   **QR Connectivity**: Easily add friends by scanning their unique profile QR codes.
*   **Friend Profiles**: Deep dive into your friends' emotional trends (if shared) to understand them better.

### 🛠️ Core Functionalities
*   **Reminders**: Set up custom notifications to ensure you never miss a daily mood check-in.
*   **Offline First**: Built with Room database to ensure your data is always accessible, syncing seamlessly with the cloud when you're back online.
*   **Secure Authentication**: Integrated with Supabase and Google Credentials for a frictionless and secure login experience.

---

## 🚀 Tech Stack

MyEmotions is built with the latest Android development tools and best practices:

*   **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern, declarative UI.
*   **Design System**: [Material 3](https://m3.material.io/) - The latest evolution of Material Design.
*   **Architecture**: MVVM (Model-View-ViewModel) with Clean Architecture principles.
*   **Dependency Injection**: [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) - Standard DI for Android.
*   **Local Storage**: [Room](https://developer.android.com/training/data-storage/room) - Robust local database.
*   **Background Tasks**: [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) - Reliable background synchronization.
*   **Networking & Backend**: [Supabase](https://supabase.com/) - Auth and Realtime Database.
*   **Image Loading**: [Coil](https://coil-kt.github.io/coil/) - Kotlin-first image loading library.
*   **Hardware Integration**: [CameraX](https://developer.android.com/training/camerax) for emotion capture and [Google Maps SDK](https://developers.google.com/maps/documentation/android-sdk/overview) for the Space Map.

---

## 📸 Screenshots

| Dashboard | Emotion Capture | Space Map |
| :---: | :---: | :---: |
| ![Dashboard](https://via.placeholder.com/200x400?text=Dashboard) | ![Capture](https://via.placeholder.com/200x400?text=Capture) | ![Map](https://via.placeholder.com/200x400?text=Space+Map) |

*(Note: Replace placeholders with actual screenshots of the app)*

---

## 🛠️ Getting Started

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/pranayharjai/MyEmotions.git
    ```
2.  **Open in Android Studio**:
    Make sure you have the latest version of Android Studio (Ladybug or newer recommended).
3.  **Setup Supabase**:
    *   Create a project on Supabase.
    *   Add your `SUPABASE_URL` and `SUPABASE_ANON_KEY` to your local properties or secrets.
4.  **Google Maps API**:
    *   Obtain an API key from the [Google Cloud Console](https://console.cloud.google.com/).
    *   Add it to your `AndroidManifest.xml` or `secrets.properties`.
5.  **Build and Run**:
    Connect an Android device or emulator and hit **Run**.

---

## 🤝 Contributing

Contributions are welcome! If you have suggestions for new features or improvements, feel free to open an issue or submit a pull request.

1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4.  Push to the Branch (`git push origin feature/AmazingFeature`)
5.  Open a Pull Request

---

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.

---

Made with ❤️ by [Pranay Harjai](https://github.com/pranayharjai)
