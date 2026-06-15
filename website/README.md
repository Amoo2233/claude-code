# Nova — a modern starter website

A fast, responsive, accessible single-page website built with plain HTML, CSS,
and vanilla JavaScript. No build step, no dependencies — open `index.html` and it works.

## Features

- **Dark & light themes** — toggle in the header, respects system preference, remembers your choice.
- **Fully responsive** — fluid layout from 320px phones to ultrawide screens.
- **Token-driven design** — colors, radii, and spacing live in CSS custom properties at the top of `styles.css`.
- **Smooth animations** — scroll-reveal sections, animated stat counters, and a logo marquee.
- **Accessible** — semantic landmarks, keyboard navigation, visible focus states, ARIA, and `prefers-reduced-motion` support.
- **Sections included** — sticky nav, hero, social-proof marquee, features, showcase, pricing, FAQ, email CTA, footer.

## Run it

Just open the file:

```bash
open website/index.html        # macOS
xdg-open website/index.html    # Linux
```

Or serve it locally:

```bash
cd website
python3 -m http.server 8000
# visit http://localhost:8000
```

## Customize

Edit the tokens at the top of `styles.css`:

```css
:root {
  --accent: #6d5efc;   /* primary brand color */
  --accent-2: #00d4ff; /* gradient partner     */
  --radius-lg: 22px;   /* card corner radius   */
}
```

Every component reads from these tokens, so a few edits restyle the whole site.

## Files

| File         | Purpose                                  |
|--------------|------------------------------------------|
| `index.html` | Markup and content for all sections      |
| `styles.css` | Design tokens, layout, components, themes |
| `script.js`  | Theme toggle, menu, reveal, counters, form |
