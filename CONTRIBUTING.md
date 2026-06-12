# Contributing to Zarki

Thanks for your interest in improving Zarki! Contributions of all kinds are welcome — bug fixes, new features, UI polish, documentation, and ideas.

## How to contribute

1. **Fork** the repository (button at the top-right on GitHub).
2. **Clone** your fork and open it in Android Studio.
3. Create a branch: `git checkout -b my-change`
4. Make your changes. Try to match the existing code style (Kotlin + Jetpack Compose, MVVM).
5. Build and test on a device/emulator: `./gradlew assembleDebug`
6. Commit and push to your fork.
7. Open a **Pull Request** back to this repo, describing what you changed and why.

## Reporting bugs / suggesting features

Open an **Issue** with:
- What you expected to happen
- What actually happened (and steps to reproduce, for bugs)
- Your device + Android version (for bugs)

## Project structure

```
data/      networking (MangaDex API), Room database, downloads, settings, sources
domain/    core models (Manga, Chapter)
ui/        Compose screens + ViewModels (browse, detail, reader, library, history, downloads, settings, about)
```

## Ground rules

- Keep Zarki clean and legal — it uses the official **MangaDex API** only. Please don't add piracy/scraper sources.
- Be respectful in issues and pull requests.

## License of contributions

By submitting a contribution you agree it is licensed to the author (Tughral Khattak) and becomes part of Zarki under the project [LICENSE](LICENSE), with the author retaining ownership of the combined work.

---

Happy contributing! 🎉
