# AI Wallpapers — Android

The Android counterpart of the iOS app in `../AIWallpapers`: a Kotlin /
Jetpack Compose app with the **same catalog of 500 AI-generated wallpapers**
across 10 categories. Free users download every wallpaper in **480p**;
Premium users (Google Play subscription or lifetime purchase) unlock
**4K Ultra HD** (2160×3840 portrait).

## Features

- **500 wallpapers, 10 categories** — the identical
  `app/src/main/assets/wallpapers.json` manifest used by the iOS app, so both
  platforms share one catalog and one CDN.
- **Browse, search, categories, favorites** with a lazy Compose grid.
- **Premium with Google Play Billing 7** — monthly subscription
  (`premium_monthly`) and lifetime unlock (`premium_lifetime`), with
  acknowledgement and automatic restore from the Google account.
- **Quality gating** — `PremiumManager.maxQuality` resolves to 480p (free)
  or 4K (premium) and drives preview, download, and set-wallpaper.
- **Download to gallery** — original JPEG bytes written via MediaStore into
  `Pictures/AI Wallpapers` (no recompression, no storage permission needed
  on Android 10+).
- **Set as wallpaper directly** — Android-only bonus using `WallpaperManager`.

## Project layout

```
AIWallpapersAndroid/
├── settings.gradle.kts / build.gradle.kts / gradle.properties
└── app/
    ├── build.gradle.kts              Compose, Coil, Billing, serialization
    └── src/main/
        ├── AndroidManifest.xml
        ├── assets/wallpapers.json    The 500-wallpaper manifest
        └── java/com/aiwallpapers/app/
            ├── MainActivity.kt
            ├── model/                Wallpaper, quality tiers, CatalogConfig
            ├── data/                 Catalog, favorites, download/set actions
            ├── billing/              PremiumManager (Play Billing)
            └── ui/                   Compose screens + theme
```

## Running the app

1. Open the `AIWallpapersAndroid` folder in **Android Studio** (Ladybug or
   newer); it configures Gradle automatically.
2. Run on any device/emulator with **Android 10 (API 29)+**.
3. The app ships in **demo mode** (`CatalogConfig.DEMO_MODE = true`): it
   shows deterministic placeholder images so everything is browsable before
   the AI images exist.

Billing note: Play Billing only returns products when the app is distributed
through Google Play (an **internal testing** track is enough). Sideloaded
debug builds show "Products unavailable" on the paywall — everything else
works.

## Pointing at the real 500 AI wallpapers

The images are produced once by the shared pipeline in
`../AIWallpapers/tools/` (see that README): `generate_wallpapers.py` renders
`4k/<id>.jpg` + `480p/<id>.jpg` for all 500 prompts. Upload the output to a
CDN, then in `model/Wallpaper.kt` set `CatalogConfig.BASE_URL` to the public
URL and `DEMO_MODE = false`. If you regenerate the manifest, copy it to
`app/src/main/assets/wallpapers.json` as well.

## Shipping checklist

- Create the products in Google Play Console: a subscription with product ID
  `premium_monthly` and an in-app product `premium_lifetime`.
- Replace the placeholder adaptive launcher icon.
- Set your own `applicationId` (currently `com.aiwallpapers.app`) and signing
  config before uploading the release bundle.
