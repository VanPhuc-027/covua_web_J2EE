(function () {
    const STORAGE_KEY = "chess_theme";
    const DEFAULT_THEME = "dark";

    function getSavedTheme() {
        try {
            const savedTheme = localStorage.getItem(STORAGE_KEY);
            return savedTheme === "light" || savedTheme === "dark"
                ? savedTheme
                : DEFAULT_THEME;
        } catch (error) {
            console.warn("Không đọc được theme từ localStorage:", error);
            return DEFAULT_THEME;
        }
    }

    function saveTheme(theme) {
        try {
            localStorage.setItem(STORAGE_KEY, theme);
        } catch (error) {
            console.warn("Không lưu được theme vào localStorage:", error);
        }
    }

    function applyTheme(theme) {
        const finalTheme = theme === "light" ? "light" : "dark";

        document.documentElement.setAttribute("data-theme", finalTheme);

        if (document.body) {
            if (finalTheme === "light") {
                document.body.classList.add("light-mode");
            } else {
                document.body.classList.remove("light-mode");
            }
        }

        const themeSelect = document.getElementById("themeSelect");
        if (themeSelect) {
            themeSelect.value = finalTheme;
        }

        const themeToggle = document.getElementById("themeToggle");
        if (themeToggle) {
            themeToggle.checked = finalTheme === "dark";
        }
    }

    function bindThemeControls() {
        const themeSelect = document.getElementById("themeSelect");
        if (themeSelect) {
            themeSelect.value = getSavedTheme();

            themeSelect.addEventListener("change", function () {
                const selectedTheme = themeSelect.value === "light" ? "light" : "dark";
                saveTheme(selectedTheme);
                applyTheme(selectedTheme);
            });
        }

        const themeToggle = document.getElementById("themeToggle");
        if (themeToggle) {
            themeToggle.checked = getSavedTheme() === "dark";

            themeToggle.addEventListener("change", function () {
                const selectedTheme = themeToggle.checked ? "dark" : "light";
                saveTheme(selectedTheme);
                applyTheme(selectedTheme);
            });
        }
    }

    function initTheme() {
        applyTheme(getSavedTheme());

        document.addEventListener("DOMContentLoaded", function () {
            applyTheme(getSavedTheme());
            bindThemeControls();
        });
    }

    initTheme();

    window.ThemeManager = {
        getSavedTheme: getSavedTheme,
        saveTheme: saveTheme,
        applyTheme: applyTheme
    };
})();