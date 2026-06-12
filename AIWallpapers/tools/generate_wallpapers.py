#!/usr/bin/env python3
"""Render the 500 AI wallpapers from wallpapers.json.

For every entry in the manifest this script:
  1. Generates a portrait image with the Stability AI API (SDXL-class model).
  2. Upscales/resizes to 4K portrait (2160x3840) for premium users.
  3. Downscales to 480p portrait (480x854) for free users.

Output layout (matches what the app expects on your CDN):
    output/4k/<id>.jpg
    output/480p/<id>.jpg

Usage:
    export STABILITY_API_KEY=sk-...
    pip install -r requirements.txt
    python3 generate_wallpapers.py [--manifest path] [--output dir] [--limit N]

The script is resumable: wallpapers whose 4K file already exists are
skipped, so it can be re-run after rate limits or interruptions.

After generation, upload the output directory to your CDN/object storage
(e.g. `aws s3 sync output/ s3://your-bucket/wallpapers/`) and set
`CatalogConfig.baseURL` in the app to the public base URL.
"""

import argparse
import io
import json
import os
import sys
import time
from pathlib import Path

import requests
from PIL import Image

API_URL = "https://api.stability.ai/v2beta/stable-image/generate/core"
SIZES = {"4k": (2160, 3840), "480p": (480, 854)}
JPEG_QUALITY = {"4k": 92, "480p": 80}


def generate_image(prompt: str, api_key: str) -> bytes:
    response = requests.post(
        API_URL,
        headers={"authorization": f"Bearer {api_key}", "accept": "image/*"},
        files={"none": ""},
        data={
            "prompt": prompt,
            "aspect_ratio": "9:16",
            "output_format": "png",
            "negative_prompt": "text, watermark, logo, low quality, blurry, deformed",
        },
        timeout=180,
    )
    if response.status_code == 429:
        raise RuntimeError("rate-limited")
    response.raise_for_status()
    return response.content


def write_variants(png_bytes: bytes, wallpaper_id: str, output_dir: Path) -> None:
    source = Image.open(io.BytesIO(png_bytes)).convert("RGB")
    for label, size in SIZES.items():
        variant = source.resize(size, Image.LANCZOS)
        path = output_dir / label / f"{wallpaper_id}.jpg"
        path.parent.mkdir(parents=True, exist_ok=True)
        variant.save(path, "JPEG", quality=JPEG_QUALITY[label], optimize=True)


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--manifest",
        type=Path,
        default=Path(__file__).resolve().parent.parent
        / "AIWallpapers" / "Resources" / "wallpapers.json",
    )
    parser.add_argument("--output", type=Path, default=Path(__file__).parent / "output")
    parser.add_argument("--limit", type=int, default=None,
                        help="only generate the first N missing wallpapers")
    args = parser.parse_args()

    api_key = os.environ.get("STABILITY_API_KEY")
    if not api_key:
        print("error: set STABILITY_API_KEY (https://platform.stability.ai)", file=sys.stderr)
        return 1

    wallpapers = json.loads(args.manifest.read_text())["wallpapers"]
    pending = [w for w in wallpapers
               if not (args.output / "4k" / f"{w['id']}.jpg").exists()]
    if args.limit:
        pending = pending[: args.limit]
    print(f"{len(wallpapers)} wallpapers in manifest, {len(pending)} to generate")

    for index, wallpaper in enumerate(pending, 1):
        for attempt in range(5):
            try:
                png = generate_image(wallpaper["prompt"], api_key)
                write_variants(png, wallpaper["id"], args.output)
                print(f"[{index}/{len(pending)}] {wallpaper['id']} {wallpaper['title']}")
                break
            except Exception as error:  # noqa: BLE001 - retry then surface
                wait = 2 ** (attempt + 1)
                print(f"  {wallpaper['id']} attempt {attempt + 1} failed ({error}); "
                      f"retrying in {wait}s")
                time.sleep(wait)
        else:
            print(f"error: giving up on {wallpaper['id']}; re-run to resume",
                  file=sys.stderr)
            return 1
    print("done")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
