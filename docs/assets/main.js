// Shared site behavior: copy-to-clipboard buttons + cursor-driven 3D tilt.

document.addEventListener("click", (e) => {
  const btn = e.target.closest(".copy-btn");
  if (!btn) return;
  navigator.clipboard.writeText(btn.dataset.copy).then(() => {
    btn.textContent = "copied";
    btn.classList.add("copied");
    setTimeout(() => {
      btn.textContent = "copy";
      btn.classList.remove("copied");
    }, 1600);
  });
});

// --- 3D tilt -----------------------------------------------------------
// Elements matching TILT_SELECTOR tilt toward the cursor with a moving
// glare. Event delegation means dynamically added elements (changelog
// release cards) get the effect for free. Disabled for touch devices and
// users who prefer reduced motion.

const TILT_SELECTOR = ".card, .install, .term, .release";
const MAX_TILT = 6; // degrees
const PERSPECTIVE = 900; // px

const motionOk =
  window.matchMedia("(prefers-reduced-motion: no-preference)").matches &&
  window.matchMedia("(pointer: fine)").matches;

if (motionOk) {
  let active = null;
  let raf = 0;
  let lastEvent = null;

  const reset = (el) => {
    el.classList.remove("tilting");
    el.style.transform = "";
  };

  const apply = () => {
    raf = 0;
    if (!active || !lastEvent) return;
    const r = active.getBoundingClientRect();
    const px = (lastEvent.clientX - r.left) / r.width; // 0..1
    const py = (lastEvent.clientY - r.top) / r.height; // 0..1
    const rx = (0.5 - py) * MAX_TILT * 2;
    const ry = (px - 0.5) * MAX_TILT * 2;
    active.style.transform =
      `perspective(${PERSPECTIVE}px) rotateX(${rx.toFixed(2)}deg) rotateY(${ry.toFixed(2)}deg) scale3d(1.015, 1.015, 1.015)`;
    active.style.setProperty("--mx", `${(px * 100).toFixed(1)}%`);
    active.style.setProperty("--my", `${(py * 100).toFixed(1)}%`);
  };

  document.addEventListener("mousemove", (e) => {
    const el = e.target.closest(TILT_SELECTOR);
    if (el !== active) {
      if (active) reset(active);
      active = el;
      if (active) {
        active.classList.add("tilt", "tilting");
      }
    }
    if (!active) return;
    lastEvent = e;
    if (!raf) raf = requestAnimationFrame(apply);
  });

  document.addEventListener("mouseleave", () => {
    if (active) reset(active);
    active = null;
  });
}
