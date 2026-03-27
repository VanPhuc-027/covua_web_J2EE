document.addEventListener("DOMContentLoaded", function () {
    const toggleSound = document.getElementById("toggleSound");
    const toggleHints = document.getElementById("toggleHints");
    const toggleFocus = document.getElementById("toggleFocus");
    const saveBtn = document.getElementById("saveSettingsBtn");

    // Load trạng thái đã lưu
    if (toggleSound) {
        toggleSound.checked = localStorage.getItem("chess_sound") !== "false";
    }

    if (toggleHints) {
        toggleHints.checked = localStorage.getItem("chess_hints") !== "false";
    }

    if (toggleFocus) {
        toggleFocus.checked = localStorage.getItem("chess_focus") === "true";
        applyFocusMode(toggleFocus.checked);
    }

    function applyFocusMode(enabled) {
        if (enabled) {
            document.body.classList.add("focus-mode");
        } else {
            document.body.classList.remove("focus-mode");
        }
    }

    function saveSettings() {
        const soundEnabled = toggleSound ? toggleSound.checked : true;
        const hintsEnabled = toggleHints ? toggleHints.checked : true;
        const focusEnabled = toggleFocus ? toggleFocus.checked : false;

        localStorage.setItem("chess_sound", soundEnabled);
        localStorage.setItem("chess_hints", hintsEnabled);
        localStorage.setItem("chess_focus", focusEnabled);

        applyFocusMode(focusEnabled);

        // phát âm thanh test nếu bật âm thanh
        if (soundEnabled) {
            const testAudio = new Audio("/sounds/move-self.mp3");
            testAudio.play().catch(function () {
                console.warn("Không phát được âm thanh test.");
            });
        }

        // hiệu ứng nút lưu
        if (saveBtn) {
            const textSpan = saveBtn.querySelector("span");
            const originalText = textSpan ? textSpan.textContent : saveBtn.textContent;

            if (textSpan) {
                textSpan.textContent = "Đã lưu ✓";
            } else {
                saveBtn.textContent = "Đã lưu ✓";
            }

            saveBtn.disabled = true;

            setTimeout(function () {
                if (textSpan) {
                    textSpan.textContent = originalText;
                } else {
                    saveBtn.textContent = originalText;
                }
                saveBtn.disabled = false;
            }, 1500);
        }
    }

    // click nút lưu
    if (saveBtn) {
        saveBtn.addEventListener("click", saveSettings);
    }

    // đổi focus mode ngay khi gạt
    if (toggleFocus) {
        toggleFocus.addEventListener("change", function () {
            applyFocusMode(toggleFocus.checked);
        });
    }

    // cho file khác dùng nếu cần
    window.saveSettings = saveSettings;
});