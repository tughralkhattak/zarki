# 📦 Publishing Zarki

A step-by-step guide to getting Zarki into people's hands — from free options to the Google Play Store.

---

## 🔑 FIRST: protect your signing key (very important)

Your app is signed with `zarki-release.keystore` (password in `keystore.properties`).

- **Back this file up somewhere safe** (cloud drive, USB) and remember the password.
- If you ever publish on Google Play, **every future update must be signed with this same key.** Lose it = you can never update your app again; you'd have to publish a brand-new listing.
- It is **git-ignored on purpose** — never upload it publicly.

---

## ✅ Option 1 — GitHub Releases (FREE, do this first)

Gives you a public link anyone can download from: `github.com/tughralkhattak/zarki/releases`

1. The signed APK is at `app/build/outputs/apk/release/app-release.apk`.
2. A GitHub Release has been created with it attached (or run:
   `gh release create v1.0 app/build/outputs/apk/release/app-release.apk -t "Zarki v1.0" -n "First release"`).
3. Share the release link. Users tap the APK → allow "install unknown apps" → done.

**Cost: $0. Reach: anyone with the link.**

---

## ✅ Option 2 — Amazon Appstore (FREE store listing)

Unlike Google, Amazon's developer account is **free**.

1. Go to **developer.amazon.com**, sign in, register as a developer (free).
2. **Apps & Games → Add New App → Android.**
3. Upload `app-release.apk`.
4. Fill in title, description, screenshots, icon (assets below).
5. Submit for review.

**Cost: $0. Reach: real app store, Amazon devices + Android users.**

---

## 💳 Option 3 — Google Play Store ($25 one-time)

The biggest reach. Requires a one-time **$25** developer fee.

1. **play.google.com/console** → pay the one-time $25, create your developer account.
2. **Create app** → name **"Zarki - Manga Reader"**, language, free app.
3. Google Play wants an **App Bundle (.aab)**, not an APK. Build it:
   ```
   gradlew bundleRelease
   ```
   Output: `app/build/outputs/bundle/release/app-release.aab` — upload this.
4. Complete the required sections:
   - **Store listing** (title, short + full description, screenshots, icon, feature graphic)
   - **Content rating** questionnaire
   - **Data safety** form (Zarki stores data only on-device, collects nothing → easy)
   - **Privacy policy** URL (you can host a simple one free on GitHub Pages)
   - **Target audience**
5. Create a **Production release**, upload the `.aab`, roll out.

> Note: Google reviews manga apps. Because Zarki uses only the **MangaDex public API** and bundles **no piracy sources**, it's in good shape — keep it that way.

---

## 🎨 Store assets you'll need (for any store)

- **App icon** 512×512 PNG (Zarki's purple "Z").
- **Screenshots** — at least 2 phone screenshots (browse, reader, library). Take them on your phone or in Android Studio's emulator.
- **Short description** (≤80 chars):
  > A fast, beautiful, ad-free manga reader. Read online or offline.
- **Full description** (draft):
  > Zarki is a clean, modern, lightning-fast manga reader for Android. Browse and
  > search a huge catalogue, read in webtoon, left-to-right, or right-to-left mode,
  > save favourites to your library, track your history, and download chapters to
  > read offline anywhere. No ads, no clutter — just a beautiful reading experience.
- **Privacy policy** (Zarki collects no personal data, stores everything on-device — a one-paragraph policy is enough).

---

## 🔁 Releasing an update later
1. Bump `versionCode` (and `versionName`) in `app/build.gradle.kts`.
2. Rebuild the signed `.apk` / `.aab` with the **same keystore**.
3. Upload the new file to the same listing.

---

© 2026 Tughral Khattak. All rights reserved.
