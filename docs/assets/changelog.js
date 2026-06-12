// Changelog viewer: fetches CHANGELOG.md from the repo, parses its
// "## version" / "- bullet" structure, and renders searchable release cards.

const SOURCES = [
  // Same-origin copy first (works when the site is served from the repo root),
  // then raw GitHub as the canonical, always-fresh fallback.
  "../CHANGELOG.md",
  "https://raw.githubusercontent.com/anthropics/claude-code/main/CHANGELOG.md",
];

const PAGE_SIZE = 25;

const releasesEl = document.getElementById("releases");
const searchEl = document.getElementById("search");
const countEl = document.getElementById("count");
const moreBtn = document.getElementById("load-more");

let releases = [];
let filtered = [];
let shown = 0;

function parseChangelog(md) {
  const out = [];
  let current = null;
  for (const line of md.split("\n")) {
    const version = line.match(/^##\s+(.+)/);
    if (version) {
      current = { version: version[1].trim(), items: [] };
      out.push(current);
    } else if (current && /^\s*[-*]\s+/.test(line)) {
      current.items.push(line.replace(/^\s*[-*]\s+/, "").trim());
    }
  }
  return out;
}

function escapeHtml(s) {
  return s.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
}

// Minimal inline markdown: `code`, **bold**, [text](url).
function renderInline(s) {
  return escapeHtml(s)
    .replace(/`([^`]+)`/g, "<code>$1</code>")
    .replace(/\*\*([^*]+)\*\*/g, "<strong>$1</strong>")
    .replace(
      /\[([^\]]+)\]\((https?:\/\/[^)\s]+)\)/g,
      '<a href="$2" target="_blank" rel="noopener">$1</a>'
    );
}

function renderRelease(release, isLatest) {
  const items = release.items.map((i) => `<li>${renderInline(i)}</li>`).join("");
  return `
    <article class="release">
      <div class="release-head">
        <span class="version">v${escapeHtml(release.version)}</span>
        ${isLatest ? '<span class="badge">latest</span>' : ""}
      </div>
      <ul>${items}</ul>
    </article>`;
}

function renderPage(reset) {
  if (reset) {
    releasesEl.innerHTML = "";
    shown = 0;
  }
  const next = filtered.slice(shown, shown + PAGE_SIZE);
  releasesEl.insertAdjacentHTML(
    "beforeend",
    next.map((r) => renderRelease(r, r === releases[0])).join("")
  );
  shown += next.length;
  moreBtn.hidden = shown >= filtered.length;
  countEl.textContent = filtered.length
    ? `${filtered.length} release${filtered.length === 1 ? "" : "s"}`
    : "no matches";
  if (!filtered.length) {
    releasesEl.innerHTML = '<div class="status">No releases match that filter.</div>';
  }
}

function applyFilter() {
  const q = searchEl.value.trim().toLowerCase();
  filtered = !q
    ? releases
    : releases.filter(
        (r) =>
          r.version.toLowerCase().includes(q) ||
          r.items.some((i) => i.toLowerCase().includes(q))
      );
  renderPage(true);
}

async function load() {
  for (const url of SOURCES) {
    try {
      const res = await fetch(url);
      if (!res.ok) continue;
      releases = parseChangelog(await res.text());
      if (releases.length) {
        filtered = releases;
        renderPage(true);
        return;
      }
    } catch {
      // try next source
    }
  }
  releasesEl.innerHTML =
    '<div class="status">Could not load the changelog. ' +
    'View it directly on <a href="https://github.com/anthropics/claude-code/blob/main/CHANGELOG.md">GitHub</a>.</div>';
}

searchEl.addEventListener("input", applyFilter);
moreBtn.addEventListener("click", () => renderPage(false));
load();
