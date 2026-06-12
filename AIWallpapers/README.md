# AI Wallpapers — iOS

A SwiftUI iPhone app with a catalog of **500 AI-generated wallpapers** across
10 categories. Free users download every wallpaper in **480p**; Premium users
(StoreKit subscription or lifetime purchase) unlock **4K Ultra HD**
(2160×3840 portrait).

## Features

- **500 wallpapers, 10 categories** (Abstract, Nature, Space, Cyberpunk,
  Minimal, Ocean, Mountains, Animals, Architecture, Fantasy) — driven by
  `AIWallpapers/Resources/wallpapers.json`, each with its own AI prompt.
- **Browse, search, categories, favorites** with a fast lazy grid.
- **Premium with StoreKit 2** — monthly subscription
  (`com.aiwallpapers.premium.monthly`) and lifetime unlock
  (`com.aiwallpapers.premium.lifetime`). Entitlements verified on-device,
  restore supported, live transaction updates.
- **Quality gating** — `PremiumManager.maxQuality` resolves to 480p (free)
  or 4K (premium); the detail view, preview, and download all respect it.
- **Save to Photos** — original JPEG bytes written via PhotoKit (add-only
  permission), so 4K files are saved without recompression.

## Project layout

```
AIWallpapers/
├── AIWallpapers.xcodeproj        Xcode 16 project (file-system synced groups)
├── AIWallpapers/
│   ├── AIWallpapersApp.swift     App entry point
│   ├── Models/                   Wallpaper, quality tiers
│   ├── Services/                 Catalog, StoreKit, favorites, photo saving
│   ├── Views/                    Browse / Categories / Detail / Paywall / …
│   ├── Resources/wallpapers.json The 500-wallpaper manifest
│   └── Products.storekit         Local StoreKit test configuration
└── tools/
    ├── generate_manifest.py      Regenerates wallpapers.json (500 prompts)
    └── generate_wallpapers.py    Renders all 500 images in 4K + 480p
```

## Running the app

1. Open `AIWallpapers.xcodeproj` in **Xcode 16+**, select your team, build to
   a simulator or device (iOS 17+).
2. The app ships in **demo mode** (`CatalogConfig.demoMode = true`): it shows
   deterministic placeholder images so everything is browsable before the AI
   images exist.
3. To test purchases locally, edit the scheme → Run → Options → StoreKit
   Configuration → `Products.storekit`.

## Generating the real 500 AI wallpapers

The images are too large to live in git (500 × 4K ≈ several GB), so they are
produced by a pipeline and served from your CDN:

```bash
cd tools
pip install -r requirements.txt
export STABILITY_API_KEY=sk-...        # https://platform.stability.ai
python3 generate_wallpapers.py         # resumable; ~500 API calls
```

This writes `tools/output/4k/<id>.jpg` (2160×3840, premium) and
`tools/output/480p/<id>.jpg` (480×854, free). Then:

1. Upload: `aws s3 sync tools/output/ s3://your-bucket/wallpapers/` (or any
   static host/CDN).
2. In `Services/CatalogConfig.swift`, set `baseURL` to the public URL and
   `demoMode = false`.

To change the catalog itself (categories, prompt styles, count), edit and
re-run `tools/generate_manifest.py`.

## Shipping checklist

- Create the two in-app purchases in App Store Connect with the product IDs
  above (and a "Premium" subscription group for the monthly plan).
- Replace the placeholder 1024×1024 app icon in `Assets.xcassets/AppIcon`.
- Set your real bundle identifier (currently `com.aiwallpapers.app`) and team.
- App Review requires a restore button (in Settings and the paywall — done)
  and terms/privacy links on the paywall for subscriptions — add your URLs.
