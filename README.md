# 📚 Zarki

A modern, fast **manga reader for Android**, built with Kotlin and Jetpack Compose. Browse and read manga from the **official, legal [MangaDex](https://mangadex.org) API**, save favourites to your library, and read in a smooth full-screen reader.

Inspired by apps like Mihon/Tachiyomi — rebuilt from the ground up on a clean, modern stack.

> ⚖️ **Legal by design:** Zarki only uses MangaDex's official public API. It is not a piracy tool and ships with no illegal sources.

---

## ✨ Features (v1)
- **Browse** the most popular manga, with infinite cover grid.
- **Search** the entire MangaDex catalogue (debounced, fast).
- **Manga details** — cover, author, status, description, full chapter list.
- **Reader** — smooth vertical/webtoon page reader with progressive image loading.
- **Library** — save favourites locally (Room database); persists offline.
- **Offline downloads** — save chapters and read with no internet.
- **History** — "continue reading" picks up where you left off.
- **Backup & restore** — export your library + history to a file, restore any time.
- **Open in browser / share** any manga.
- **Material 3** themes (incl. **AMOLED**), edge-to-edge, adaptive cover grid.

### 🔒 Private & lightweight
**No ads. No trackers. No analytics. No account.** Your data never leaves your device, and the whole app is a fraction of the size of other manga readers.

## 🏗️ Architecture
A clean, modern Android stack — MVVM with a unidirectional state flow:

| Layer | Tech |
|---|---|
| UI | Jetpack Compose + Material 3, Navigation-Compose |
| State | ViewModel + Kotlin `StateFlow`, `collectAsStateWithLifecycle` |
| Networking | Retrofit + OkHttp + kotlinx.serialization |
| Images | Coil (async, cached) |
| Local storage | Room (library favourites) |
| Async | Kotlin Coroutines + Flow |

```
data/
  remote/    Retrofit API + DTOs + network client (MangaDex)
  local/     Room database + library DAO
  MangaRepository.kt   maps API → clean domain models
domain/      Manga, Chapter models
ui/
  browse/ detail/ reader/ library/   screens + view models
  components/  shared composables (cover card)
  theme/       Material 3 theme
  ZarkiApp.kt  navigation graph + bottom nav
```

## 🚀 Build & run
**Requirements:** Android Studio (JDK 17+), Android SDK 35.

```bash
git clone https://github.com/zarkidev/zarki
cd zarki
# open in Android Studio, or from the command line:
./gradlew assembleDebug
```
The debug APK lands in `app/build/outputs/apk/debug/`. Install it on a device with `adb install`, or just press ▶ Run in Android Studio.

> Create a `local.properties` with `sdk.dir=/path/to/Android/Sdk` (Android Studio does this automatically).

## 🗺️ Roadmap
- Reading progress + "continue reading"
- Reader settings (paged mode, RTL, zoom)
- Multiple languages & content filters
- Download chapters for offline reading
- Tracker integration (AniList/MyAnimeList)
- More sources behind a clean source interface

## 🤝 Contributing
Contributions are welcome! Whether it's a bug fix, a new feature, or an idea:
1. **Fork** this repository
2. Create a branch (`git checkout -b my-feature`)
3. Make your changes and commit them
4. **Open a pull request**

Bug reports and feature ideas are also welcome via the **[Issues](https://github.com/zarkidev/zarki/issues)** tab. See [CONTRIBUTING.md](CONTRIBUTING.md) for details. By contributing, your changes become part of Zarki under its license.

## 👤 Author & Ownership
Designed and built by **Zarki**.

© 2026 Zarki. **All rights reserved.** Zarki and its source code are the
property of the author — see [LICENSE](LICENSE). Manga content is provided by the
MangaDex API and belongs to its respective creators.

## 📱 Compatibility
Runs on **Android 7.0 (Nougat) and newer** — covering virtually every active Android device.
