document.addEventListener("DOMContentLoaded", function () {

    window.showModal = function (icon, title, message, type, confirmCallback, cancelCallback) {
        const overlay = document.getElementById("game-modal-overlay");
        if (!overlay) return;

        if (overlay.dataset.timeoutId) {
            clearTimeout(parseInt(overlay.dataset.timeoutId));
            delete overlay.dataset.timeoutId;
        }

        document.getElementById("game-modal-icon").innerText = icon;
        document.getElementById("game-modal-title").innerText = title;
        document.getElementById("game-modal-message").innerText = message;

        const btnConfirm = document.getElementById("game-modal-btn-confirm");
        const btnCancel = document.getElementById("game-modal-btn-cancel");
        const btnOk = document.getElementById("game-modal-btn-ok");

        const newConfirm = btnConfirm.cloneNode(true);
        const newCancel = btnCancel.cloneNode(true);
        const newOk = btnOk.cloneNode(true);

        btnConfirm.parentNode.replaceChild(newConfirm, btnConfirm);
        btnCancel.parentNode.replaceChild(newCancel, btnCancel);
        btnOk.parentNode.replaceChild(newOk, btnOk);

        const closeModal = (immediate = false) => {
            overlay.style.opacity = "0";
            overlay.style.pointerEvents = "none";
            if (immediate) {
                overlay.style.display = "none";
            } else {
                const timeoutId = setTimeout(() => {
                    overlay.style.display = "none";
                    delete overlay.dataset.timeoutId;
                }, 300);
                overlay.dataset.timeoutId = timeoutId;
            }
        };

        if (type === "confirm") {
            newConfirm.style.display = "block";
            newCancel.style.display = "block";
            newOk.style.display = "none";

            newConfirm.onclick = () => { closeModal(); if (confirmCallback) confirmCallback(); };
            newCancel.onclick = () => { closeModal(); if (cancelCallback) cancelCallback(); };
        } else {
            newConfirm.style.display = "none";
            newCancel.style.display = "none";
            newOk.style.display = "block";

            newOk.onclick = () => { closeModal(); if (confirmCallback) confirmCallback(); };
        }

        overlay.style.display = "flex";
        void overlay.offsetWidth;
        overlay.style.opacity = "1";
        overlay.style.pointerEvents = "auto";
    };

    window.scheduleReturnToLobby = function (delaySec) {
        let remaining = delaySec || 5;
        const msgEl = document.getElementById("game-modal-message");
        const baseMsg = msgEl ? msgEl.innerText : "";
        const timer = setInterval(() => {
            remaining--;
            if (msgEl) {
                msgEl.innerHTML = baseMsg + `<br><br><span style="color:#aaa; font-size:13px;">Chuyển về sảnh sau <b style='color:#fff'>${remaining}s</b>...</span>`;
            }
            if (remaining <= 0) {
                clearInterval(timer);
                window.location.href = "/";
            }
        }, 1000);
    };


    window.renderBoardFromResponse = function (board) {
        const boxes = board?.boxes;
        if (!Array.isArray(boxes) || boxes.length !== 8) return;

        for (let r = 0; r < 8; r++) {
            const row = boxes[r];
            if (!Array.isArray(row) || row.length !== 8) continue;

            for (let c = 0; c < 8; c++) {
                const spot = row[c];
                const square = window.squareByPos.get(`${r},${c}`);
                if (!square) continue;

                const piece = spot?.piece ?? null;
                const existingImg = square.querySelector("img");

                if (!piece) {
                    if (existingImg) existingImg.remove();
                    continue;
                }

                const iconPath = piece.iconPath;
                if (typeof iconPath !== "string" || iconPath.length === 0) continue;

                if (existingImg) {
                    if (existingImg.getAttribute("src") !== iconPath) {
                        existingImg.setAttribute("src", iconPath);
                    }
                } else {
                    const img = document.createElement("img");
                    img.setAttribute("src", iconPath);
                    img.setAttribute("alt", "chess-piece");
                    square.appendChild(img);
                }
            }
        }
    };

    window.clearHighlights = function () {
        window.squares.forEach(s => {
            s.classList.remove("selected", "valid-move", "valid-capture");
        });
    };

    window.isPieceSquare = function (squareEl) {
        return !!squareEl.querySelector("img");
    };

    window.showPromotionModal = function (fR, fC, tR, tC) {
        if (!window.myColor) return;
        const overlay = document.getElementById("promotion-modal-overlay");
        const color = window.myColor.toLowerCase();

        const basePath = window.location.origin;
        document.getElementById("promo-img-queen").src = `${basePath}/img/${color}_queen.png`;
        document.getElementById("promo-img-rook").src = `${basePath}/img/${color}_rook.png`;
        document.getElementById("promo-img-bishop").src = `${basePath}/img/${color}_bishop.png`;
        document.getElementById("promo-img-knight").src = `${basePath}/img/${color}_knight.png`;

        overlay.style.display = "flex";

        const buttons = document.querySelectorAll(".promo-btn");
        buttons.forEach(btn => {
            btn.onclick = async () => {
                const piece = btn.dataset.piece;
                overlay.style.display = "none";
                await window.movePiece(fR, fC, tR, tC, piece);
                window.selectedSquare = null;
                window.clearHighlights();
            };
        });
    };


    window.renderChatMessage = function (chatMessage) {
        const messageContainer = document.getElementById("chat-messages");
        if (!messageContainer) return;
        
        if (chatMessage.type === "SYSTEM") {
            const sysMsg = document.createElement("div");
            sysMsg.style.cssText = "text-align: center; font-size: 11px; color: #777; margin: 5px 0;";
            sysMsg.innerText = chatMessage.content;
            messageContainer.appendChild(sysMsg);
        } else {
            const isMe = chatMessage.sender === window.CURRENT_USERNAME;
            const wrapper = document.createElement("div");
            wrapper.className = `message-wrapper ${isMe ? 'me' : 'them'}`;
            wrapper.innerHTML = `<div class="chat-sender">${isMe ? 'BẠN' : chatMessage.sender}</div><div class="chat-bubble">${chatMessage.content}</div>`;
            messageContainer.appendChild(wrapper);
        }
        messageContainer.scrollTop = messageContainer.scrollHeight;
    };

    window.renderMoveHistory = function (history) {
        const moveList = document.querySelector(".moves-list");
        if (!moveList || !Array.isArray(history)) return;

        moveList.innerHTML = "";
        for (let i = 0; i < history.length; i += 2) {
            const row = document.createElement("div");
            row.className = "move-row";

            const num = Math.floor(i / 2) + 1;
            const whiteMove = history[i] || "";
            const blackMove = history[i + 1] || "";

            row.innerHTML = `
                <span class="move-num">${num}.</span>
                <span class="move-white">${whiteMove}</span>
                <span class="move-black">${blackMove}</span>
            `;
            moveList.appendChild(row);
        }
        moveList.scrollTop = moveList.scrollHeight;
    };


    const DEFAULT_TIME = 180;
    window.timerSeconds = DEFAULT_TIME;
    window.timerInterval = null;
    window.currentTurn = 'WHITE';
    window.isPaused = false;

    window.pauseTimer = function () {
        window.isPaused = true;
    };

    window.resumeTimer = function () {
        window.isPaused = false;
    };

    window.resetTimer = function (newTurn) {
        if (window.timerInterval) clearInterval(window.timerInterval);
        window.currentTurn = newTurn || window.currentTurn;
        window.timerSeconds = DEFAULT_TIME;
        window.isPaused = false;
        window.updateTimerUI();

        window.timerInterval = setInterval(() => {
            if (window.isPaused) return;

            window.timerSeconds--;
            window.updateTimerUI();

            if (window.timerSeconds <= 0) {
                clearInterval(window.timerInterval);
                if (window.myColor && (window.currentTurn === window.myColor || true)) {
                    window.sendAction("TIMEOUT");
                }
            }
        }, 1000);
    };

    window.updateTimerUI = function () {
        const oppTimer = document.getElementById('timer-opponent');
        const youTimer = document.getElementById('timer-you');
        if (!oppTimer || !youTimer) return;

        oppTimer.innerText = DEFAULT_TIME + 's';
        youTimer.innerText = DEFAULT_TIME + 's';

        if (window.currentTurn === window.myColor) {
            youTimer.innerText = window.timerSeconds + 's';
            if (window.timerSeconds <= 5) youTimer.style.color = '#ff4d4d';
            else youTimer.style.color = '';
        } else {
            oppTimer.innerText = window.timerSeconds + 's';
            if (window.timerSeconds <= 5) oppTimer.style.color = '#ff4d4d';
            else oppTimer.style.color = '';
        }
    };
});
