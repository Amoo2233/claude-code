// Shared site behavior: copy-to-clipboard buttons.
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
