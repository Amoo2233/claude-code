#!/usr/bin/env python3
"""Generate the 500-wallpaper catalog manifest (wallpapers.json).

Each entry has a unique id, title, category, and a hand-tuned AI image
prompt. The manifest is bundled into the iOS app and is also the input to
generate_wallpapers.py, which renders the actual images.

Usage:
    python3 generate_manifest.py [output_path]
"""

import itertools
import json
import sys
from pathlib import Path

DEFAULT_OUTPUT = (
    Path(__file__).resolve().parent.parent
    / "AIWallpapers" / "Resources" / "wallpapers.json"
)

QUALITY_SUFFIX = (
    "vertical phone wallpaper, 9:16 portrait composition, ultra detailed, "
    "8k resolution, sharp focus, professional color grading"
)

# 10 categories x 50 wallpapers = 500. Each category combines subjects,
# styles, and moods; the cartesian product is sampled deterministically so
# every prompt is unique and re-running the script is reproducible.
CATEGORIES = {
    "Abstract": {
        "subjects": [
            "flowing liquid metal ribbons", "shattered glass geometry",
            "swirling ink in water", "layered translucent waves",
            "fractal crystal lattice", "melting gradient orbs",
            "interwoven silk threads", "exploding paint splash",
            "soft smoke spirals", "folded holographic paper",
        ],
        "styles": [
            "iridescent chrome render", "vivid neon palette",
            "muted pastel tones", "deep jewel tones", "monochrome with gold accents",
        ],
        "moods": ["dreamy", "energetic", "serene", "mysterious", "futuristic"],
    },
    "Nature": {
        "subjects": [
            "misty redwood forest at dawn", "field of lavender under storm clouds",
            "cherry blossom grove in full bloom", "autumn maple canopy",
            "moss covered waterfall", "golden wheat field at sunset",
            "frozen birch forest", "tropical rainforest canopy",
            "desert bloom of wildflowers", "bamboo grove in soft rain",
        ],
        "styles": [
            "cinematic photography", "ethereal soft focus",
            "rich HDR detail", "painterly impressionist style", "moody low light",
        ],
        "moods": ["tranquil", "majestic", "fresh", "melancholic", "vibrant"],
    },
    "Space": {
        "subjects": [
            "spiral galaxy seen from a moon", "nebula of teal and magenta gas",
            "ringed gas giant rising over ice plains", "comet passing a red dwarf",
            "astronaut drifting above Earth", "binary star sunrise",
            "black hole bending starlight", "aurora seen from orbit",
            "asteroid field in golden light", "deep field of distant galaxies",
        ],
        "styles": [
            "NASA photograph realism", "sci-fi concept art",
            "long exposure astrophotography", "retro space poster", "volumetric light render",
        ],
        "moods": ["awe inspiring", "lonely", "epic", "peaceful", "otherworldly"],
    },
    "Cyberpunk": {
        "subjects": [
            "rain soaked neon alley", "holographic koi above a night market",
            "chrome android portrait", "rooftop view of a mega city",
            "neon-lit subway platform", "cybernetic samurai in fog",
            "glitching billboard canyon", "flying cars between towers",
            "hacker den glowing with screens", "neon shrine in the rain",
        ],
        "styles": [
            "blade runner cinematography", "anime cel shading",
            "synthwave palette", "gritty photorealism", "vaporwave aesthetic",
        ],
        "moods": ["electric", "noir", "rebellious", "melancholic", "intense"],
    },
    "Minimal": {
        "subjects": [
            "single line horizon over calm sea", "lone tree on a pastel hill",
            "floating geometric monolith", "paper crane on gradient backdrop",
            "two tone color field with grain", "thin crescent moon on ink sky",
            "isolated staircase to nowhere", "soft shadow of window blinds",
            "single ripple on still water", "matte ceramic shapes in sunlight",
        ],
        "styles": [
            "flat design", "soft 3D render", "film grain photography",
            "bauhaus poster style", "japanese zen aesthetic",
        ],
        "moods": ["calm", "balanced", "airy", "contemplative", "warm"],
    },
    "Ocean": {
        "subjects": [
            "giant wave curling at golden hour", "bioluminescent plankton shoreline",
            "coral reef teeming with fish", "whale breaching under stars",
            "underwater light shafts in a kelp forest", "turquoise lagoon from above",
            "stormy lighthouse coast", "sea turtle gliding over reef",
            "arctic iceberg with deep blue base", "tide pools at sunset",
        ],
        "styles": [
            "underwater photography", "aerial drone shot",
            "cinematic teal and orange grade", "watercolor painting", "ultra realistic render",
        ],
        "moods": ["powerful", "serene", "mysterious", "playful", "vast"],
    },
    "Mountains": {
        "subjects": [
            "snow capped peak above cloud sea", "alpine lake mirror reflection",
            "jagged ridgeline at blue hour", "terraced valley in morning fog",
            "volcanic peak with lava glow", "milky way over a granite summit",
            "hiker silhouette on a knife edge", "glacier carving through rock",
            "wildflower meadow below cliffs", "monastery perched on a crag",
        ],
        "styles": [
            "national geographic photography", "matte painting",
            "minimal flat illustration", "dramatic chiaroscuro light", "infrared landscape",
        ],
        "moods": ["grand", "solitary", "adventurous", "still", "humbling"],
    },
    "Animals": {
        "subjects": [
            "snow leopard on a ridge", "macaw in rainforest light",
            "arctic fox in falling snow", "lion portrait at dusk",
            "hummingbird frozen mid flight", "elephant herd at a waterhole",
            "owl staring through pine branches", "jellyfish glowing in the deep",
            "wolf pack crossing a frozen river", "peacock feather close up",
        ],
        "styles": [
            "wildlife photography with bokeh", "detailed digital painting",
            "low poly art", "ink wash illustration", "studio portrait lighting",
        ],
        "moods": ["majestic", "tender", "fierce", "curious", "graceful"],
    },
    "Architecture": {
        "subjects": [
            "spiral staircase from below", "futuristic glass atrium",
            "ancient temple in jungle mist", "brutalist tower against storm sky",
            "moroccan tiled courtyard", "tokyo street canyon at night",
            "gothic cathedral interior", "infinity bridge over a fjord",
            "desert villa with clean lines", "venetian canal at dawn",
        ],
        "styles": [
            "leading lines composition", "symmetry photography",
            "isometric illustration", "golden hour photography", "cinematic wide angle",
        ],
        "moods": ["monumental", "intimate", "futuristic", "timeless", "dramatic"],
    },
    "Fantasy": {
        "subjects": [
            "floating islands with waterfalls", "dragon circling a crystal spire",
            "enchanted forest with glowing mushrooms", "castle in the clouds",
            "portal opening in an ancient library", "phoenix rising from embers",
            "moonlit elven city in the trees", "giant whale flying over plains",
            "wizard tower under twin moons", "frozen kingdom under aurora",
        ],
        "styles": [
            "epic fantasy concept art", "studio ghibli inspired",
            "dark fairytale illustration", "luminous oil painting", "high detail 3D render",
        ],
        "moods": ["magical", "ominous", "whimsical", "heroic", "ethereal"],
    },
}

PER_CATEGORY = 50


def build_manifest():
    wallpapers = []
    counter = 1
    for category, parts in CATEGORIES.items():
        combos = itertools.product(parts["subjects"], parts["styles"], parts["moods"])
        # 10 subjects x 5 styles x 5 moods = 250 combos; stride keeps the
        # 50 picks spread across subjects/styles instead of clustered.
        picks = list(combos)[::5][:PER_CATEGORY]
        for subject, style, mood in picks:
            wid = f"wp-{counter:03d}"
            title = subject.split(",")[0].capitalize()
            prompt = f"{subject}, {style}, {mood} atmosphere, {QUALITY_SUFFIX}"
            wallpapers.append({
                "id": wid,
                "title": title,
                "category": category,
                "prompt": prompt,
            })
            counter += 1
    return wallpapers


def main():
    output = Path(sys.argv[1]) if len(sys.argv) > 1 else DEFAULT_OUTPUT
    wallpapers = build_manifest()
    assert len(wallpapers) == 500, f"expected 500 wallpapers, got {len(wallpapers)}"
    assert len({w["prompt"] for w in wallpapers}) == 500, "prompts must be unique"
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(json.dumps({"wallpapers": wallpapers}, indent=2) + "\n")
    print(f"Wrote {len(wallpapers)} wallpapers to {output}")


if __name__ == "__main__":
    main()
