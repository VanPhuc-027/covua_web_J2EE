document.addEventListener("DOMContentLoaded", function () {
    const themeSelect = document.getElementById("themeSelect");
    const saveButton = document.querySelector(".save-settings-btn, .save-btn, button[type='submit'], .settings-save-btn");

    // Lấy theme đã lưu, mặc định là dark
    const savedTheme = localStorage.getItem("theme") || "dark";

    applyTheme(savedTheme);

    if (themeSelect) {
        themeSelect.value = savedTheme;
    }

    // Nếu có nút lưu cài đặt
    if (saveButton) {
        saveButton.addEventListener("click", function (event) {
            const selectedTheme = themeSelect ? themeSelect.value : "dark";
            localStorage.setItem("theme", selectedTheme);
            applyTheme(selectedTheme);
        });
    }

    // Nếu muốn đổi ngay khi chọn, bỏ comment phần dưới
    /*
    if (themeSelect) {
        themeSelect.addEventListener("change", function () {
            const selectedTheme = themeSelect.value;
            localStorage.setItem("theme", selectedTheme);
            applyTheme(selectedTheme);
        });
    }
    */
});

function applyTheme(theme) {
    const body = document.body;

    if (theme === "light") {
        body.classList.add("light-mode");
    } else {
        body.classList.remove("light-mode");
    }
}