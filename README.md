# 🎵 Auric Music Player - Modern Offline Music Player (Prototype)
> **Internship Project @ CODTECH IT SOLUTIONS**

| Internship Info      | Details                            |
|----------------------|-------------------------------------|
| **Name**             | Rahul Salunke                      |
| **Intern ID**        | CT04DH562                          |
| **Domain**           | Android Development                |
| **Duration**         | 6 Weeks                            |
| **Mentor**           | Neela Santhosh Kumar               |

---
### 📸 Screenshots

| Splash Screen | All Songs (Home) | Now Playing |
| :---: | :---: | :---: |
| ![Splash Screen](https://github.com/user-attachments/assets/72e55271-3493-4b25-a767-0fe753034403) | ![All Songs Screen](https://github.com/user-attachments/assets/0011b79f-9094-40db-912c-4138c4e25ae1) | ![Now Playing Screen](https://github.com/user-attachments/assets/e8a05edd-846e-439c-a9f0-042fd46e0b8f) |
| **Favorites** | **Playlists** | **Notification** |
| ![Favorites Screen](https://github.com/user-attachments/assets/e21ef89b-6405-4e07-87f2-53fd251750ea) | ![Playlists Screen](https://github.com/user-attachments/assets/7eaab470-031a-450e-845a-f99da55f41dc) | ![Notification Control](https://github.com/user-attachments/assets/91ba3cdb-14ea-43b0-bfba-7f61e0a1e82b) |

Auric Music is a modern, offline music player for Android built with Kotlin and Jetpack Compose. It allows users to play their local audio files, manage favorites, and create custom playlists in a beautiful, intuitive interface with a focus on a premium user experience.

---

## 🚀 Features

| Feature | Description |
|--------|-------------|
| 🎧 **Offline Playback** | Plays all local audio files from the device, filtering out short clips like call recordings. |
| ⏯️ **Full Playback Controls** | Full support for Play, Pause, Skip Next/Previous, Seek, Rewind, and Fast Forward. |
| ✨ **Modern UI/UX** | Built entirely with Jetpack Compose & Material 3, featuring a persistent Mini-Player and a "Now Playing" screen with swipe-to-skip gestures and an animated glowing border. |
| ❤️ **Favorites System** | Users can add/remove songs as favorites, which are saved permanently on the device. |
| 🎶 **Playlist Management** | Full C.R.U.D. for playlists: Create, Add songs, View songs, Remove songs, and Delete entire playlists. |
| 💾 **Data Persistence** | All user data (favorites, playlists) is saved locally and persists between app launches using a Room database. |
| 🔔 **Background Playback** | Music continues to play seamlessly when the app is in the background via a `MediaLibraryService` and is controllable from a system notification. |

---

## 🛠 Tech Stack

- **Kotlin** (Primary Language)
- **Jetpack Compose** (Modern UI Toolkit)
- **MVVM Architecture** (Model-View-ViewModel)
- **Kotlin Coroutines & Flow** (For asynchronous operations)
- **Room Database** (Local Persistence)
- **Jetpack Navigation Compose** (For screen navigation)
- **ExoPlayer (Media3)** (For robust audio playback)
- **Coil** (For asynchronous image loading)
- **Material 3 Components**

---
## 🔧 Installation
```bash
git clone https://github.com/therahuls916/Auric-Music-Player-Android.git
cd Auric-Music-Player-Android```
> Open the project in Android Studio, let Gradle sync, and click ▶️ Run.

---

## 📂 Folder Structure

```plaintext
app/src/main/java/com/rahul/auricmusic/
├── MainActivity.kt
├── data/
│   ├── AppDatabase.kt
│   ├── SongRepository.kt
│   ├── PlaylistDao.kt
│   ├── SongDao.kt
│   └── model/
│       ├── FavoriteSong.kt
│       ├── Playlist.kt
│       ├── PlaylistSongCrossRef.kt
│       ├── Song.kt
│       └── PlaybackState.kt
├── navigation/
│   └── BottomNavItem.kt
├── player/
│   ├── AuricMediaService.kt
│   ├── MediaControllerManager.kt
│   ├── MiniPlayer.kt
│   └── NowPlayingScreen.kt
└── ui/
    ├── screens/
    │   ├── MainAppScreen.kt
    │   ├── SplashScreen.kt
    │   ├── favorites/
    │   │   └── FavoritesScreen.kt
    │   ├── home/
    │   │   ├── HomeScreen.kt
    │   │   └── SongListItem.kt
    │   ├── playlists/
    │   │   ├── AddToPlaylistDialog.kt
    │   │   ├── PlaylistDetailsScreen.kt
    │   │   └── PlaylistsScreen.kt
    │   └── profile/
    │       └── ProfileScreen.kt
    └── theme/
        └── ... (Color.kt, Theme.kt, Type.kt)
└── viewmodel/
    ├── FavoritesViewModel.kt
    ├── HomeViewModel.kt
    ├── MainViewModel.kt
    └── PlaylistsViewModel.kt```

---

## 🔐 Permissions Used
| Permission | Reason |
|------------|--------|
| `READ_MEDIA_AUDIO` | Required to find and play music files on Android 13 and newer. |
| `READ_EXTERNAL_STORAGE` | Required for backward compatibility to find and play music files on older Android versions (API 32 and below). |
| `FOREGROUND_SERVICE` & `FOREGROUND_SERVICE_MEDIA_PLAYBACK` | Essential for allowing the music playback service to run reliably when the app is in the background, which is a requirement for all modern media apps. |

---

## 🧠 How It Works
* **UI Layer:** The entire UI is built with Jetpack Compose. Screens are stateless Composables that receive data and events from their ViewModels, creating a reactive and predictable UI.
* **State Management:** `StateFlow` is used to expose data from ViewModels to the UI. The UI observes these flows using `collectAsState` and automatically recomposes when the playback state, song lists, or user-created data changes.
* **Playback Engine:** A `MediaLibraryService` hosts the ExoPlayer instance, acting as the single source of truth for all playback. A singleton `MediaControllerManager` provides a stable connection point for the ViewModels to communicate with the service, preventing crashes and lifecycle issues.
* **Database:** Room is used as a local SQLite database to store all Favorites and Playlist data. A many-to-many relationship between Songs and Playlists is handled via a cross-reference table, allowing a song to exist in multiple playlists.

---

## ✅ Planned Features

* [ ] 🎨 Customize the Media Notification with the app's brand icon and colors.
* [ ] 🔀 Add Shuffle and Repeat controls to the "Now Playing" screen.
* [ ] 📝 Implement the full "Profile" screen UI with static information and links.
* [ ] 🖼️ Fetch missing album art from an online API (e.g., Spotify API or TheAudioDB).

---

## 🤝 Contributing
Want to help? Fork this repo, create a new branch, and open a PR with improvements or features.

---

## 📄 License
This project is licensed under the MIT License - see the `LICENSE` file for details.

---

## 👨‍💻 Developer
**Rahul Salunke**
[GitHub](https://github.com/therahuls916) | [LinkedIn](https://www.linkedin.com/in/rahulasalunke/)