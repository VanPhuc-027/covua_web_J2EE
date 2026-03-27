document.addEventListener("DOMContentLoaded", function () {
    const savedTheme = localStorage.getItem("theme") || "dark";
    applyGlobalTheme(savedTheme);
});

function applyGlobalTheme(theme) {
    if (theme === "light") {
        document.body.classList.add("light-mode");
    } else {
        document.body.classList.remove("light-mode");
    }
}