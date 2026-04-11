document.addEventListener("DOMContentLoaded", function () {
    const gameId = window.gameId;


    window.fetchAndRenderBoard = async function () {
        try {
            const res = await fetch(`/api/game/${gameId}/state`);
            if (!res.ok) return;
            const data = await res.json();
            if (data.board) {
                window.renderBoardFromResponse(data.board);
            }
            if (typeof window.handleCheckStatus === "function") {
                window.handleCheckStatus(data);
            }
        } catch (e) {
            console.error("Lỗi khi kéo trạng thái game:", e);
        }
    };


    async function loadChatHistory() {
        try {
            const response = await fetch(`/api/game/${gameId}/chat-history`);
            if (response.ok) {
                const chatHistory = await response.json();
                if (chatHistory && chatHistory.length > 0) {
                    chatHistory.forEach(chatMessage => {
                        if (typeof window.renderChatMessage === 'function') {
                            window.renderChatMessage(chatMessage);
                        }
                    });
                }
            }
        } catch (error) {
            console.error("Lỗi chat history", error);
        }
    }

    function sendChatMessage() {
        const inputField = document.getElementById("chat-input");
        const content = inputField.value.trim();
        if (content !== "" && window.stompClient) {
            const chatMessage = {
                gameId: window.CURRENT_GAME_ID,
                sender: window.CURRENT_USERNAME,
                content: content,
                type: "CHAT"
            };
            window.stompClient.send("/app/chat/" + window.CURRENT_GAME_ID, {}, JSON.stringify(chatMessage));
            inputField.value = "";
        }
    }

    const btnSendChat = document.getElementById("btn-send-chat");
    const chatInput = document.getElementById("chat-input");
    if (btnSendChat) btnSendChat.addEventListener("click", sendChatMessage);
    if (chatInput) {
        chatInput.addEventListener("keypress", function (e) {
            if (e.key === "Enter") sendChatMessage();
        });
    }

    loadChatHistory();


    window.handleCheckStatus = function (payload) {
        document.querySelectorAll(".square.checked-king").forEach(el => el.classList.remove("checked-king"));
        const statusSpan = document.querySelector(".match-status span");

        if (payload.moveHistory) {
            window.renderMoveHistory(payload.moveHistory);
        }

        if (payload.validMoves) {
            window.validMoves = payload.validMoves;
        }

        if (payload.action) {
            if (payload.message && typeof window.renderChatMessage === 'function') {
                window.renderChatMessage({
                    type: 'SYSTEM',
                    content: payload.message
                });
            }

            if (payload.action === 'OFFER_DRAW') {
                if (window.myColor && payload.actionPlayer !== window.myColor) {
                    window.pauseTimer();
                    window.showModal("🤝", "Yêu cầu Cầu hòa", payload.message || "Đối thủ muốn cầu hòa. Bạn có đồng ý không?", "confirm",
                        () => {
                            window.sendAction("ACCEPT_DRAW");
                        },
                        () => {
                            window.sendAction("DECLINE_DRAW");
                            window.resumeTimer();
                        });
                }
                return;
            }
            if (['RESIGN', 'TIMEOUT', 'ACCEPT_DRAW'].includes(payload.action)) {
                if (statusSpan) statusSpan.innerHTML = `<span style="color: #ff4d4d; font-weight: bold;">${payload.message}</span>`;
                let icon = payload.action === 'TIMEOUT' ? "⏰" : (payload.action === 'RESIGN' ? "🏳️" : "🤝");
                if (window.isSoundEnabled()) {
                    window.soundNotify.currentTime = 0;
                    window.soundNotify.play().catch(e => { });
                }
                window.showModal(icon, "Kết thúc trận đấu", payload.message, "alert", () => window.location.href = "/");
                window.scheduleReturnToLobby(5);
                if (window.timerInterval) clearInterval(window.timerInterval);
                window.clearHighlights();
                window.selectedSquare = null;
                return;
            }
        }

        if (payload.currentTurn) {
            window.resetTimer(payload.currentTurn);
            if (statusSpan && !payload.check && !payload.checkmate && !payload.action) {
                const turnName = payload.currentTurn === 'WHITE' ? 'TRẮNG' : 'ĐEN';
                statusSpan.innerHTML = `Lượt của: <b style="color: var(--primary-color); text-shadow: 0 0 5px rgba(255,255,255,0.2)">${turnName}</b>`;
            }
        }

        if (payload.checkmate) {
            const resultMsg = `CHIẾU HẾT! Quân ${payload.winner === 'WHITE' ? 'Trắng' : 'Đen'} giành chiến thắng!`;
            if (statusSpan) statusSpan.innerHTML = `<span style="color: #ff4d4d; font-weight: bold;">${resultMsg}</span>`;
            if (window.timerInterval) clearInterval(window.timerInterval);
            if (window.isSoundEnabled()) {
                window.soundNotify.currentTime = 0;
                window.soundNotify.play().catch(e => { });
            }
            window.showModal("🏆", "Chiếu hết!", resultMsg, "alert", () => window.location.href = "/");
            window.scheduleReturnToLobby(5);

            if (typeof window.renderChatMessage === 'function') {
                window.renderChatMessage({ type: 'SYSTEM', content: resultMsg });
            }
        } else if (payload.check) {
            if (statusSpan) statusSpan.innerHTML = `<span style="color: #ffaa00; font-weight: bold; animation: pulse-text 1s infinite alternate;">ĐANG BỊ CHIẾU TƯỚNG!</span>`;
            if (payload.currentTurn == window.myColor && window.isSoundEnabled()) {
                window.soundCheck.currentTime = 0;
                window.soundCheck.play().catch(e => { });
            }
            if (payload.kingRow !== undefined && payload.kingCol !== undefined) {
                const checkedKingSquare = window.squareByPos.get(`${payload.kingRow},${payload.kingCol}`);
                if (checkedKingSquare) checkedKingSquare.classList.add("checked-king");
            }
        }
    };

    window.sendAction = function (actionType) {
        if (!window.myColor) {
            console.warn("Chỉ người chơi mới có thể thực hiện hành động này.");
            return;
        }
        fetch(`/api/game/${gameId}/action`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ action: actionType })
        }).catch(e => console.error("Lỗi khi gửi hành động:", e));
    };


    function setupActionButtons() {
        const resignBtn = document.getElementById('btn-resign');
        const drawBtn = document.getElementById('btn-draw');

        if (resignBtn) {
            resignBtn.onclick = () => {
                const message = window.isBotMode ? "Bạn chắc chắn muốn đầu hàng em Bot này?" : "Bạn có chắc chắn muốn đầu hàng không?";
                window.showModal("🏳️", "Đầu hàng", message, "confirm", () => {
                    window.sendAction("RESIGN");
                });
            };
        }

        if (drawBtn) {
            drawBtn.onclick = () => {
                if (window.isBotMode) {
                    window.showModal("🤖", "Cầu hòa", "Đánh với Ta không có khái niệm cầu hòa đâu, ráng mà dánh chiến thắng đi!", "alert");
                } else {
                    window.sendAction("OFFER_DRAW");
                    window.showModal("📩", "Đã Gửi", "Đã gửi lời mời cầu hòa tới đối thủ.", "alert");
                }
            };
        }
    }

    setupActionButtons();


    if (window.isBotMode) {
        const waitingOverlay = document.querySelector(".waiting-overlay");
        if (waitingOverlay) waitingOverlay.style.display = "none";

        const oppNameEl = document.querySelector(".player.opponent .details .name");
        if (oppNameEl) {
            let diff = window.botDepth <= 6 ? "Dễ" : (window.botDepth <= 10 ? "Trung Bình" : "Khó");
            oppNameEl.innerText = "BOT_FURINA (" + diff + ")";
        }

        const matchStatusEl = document.querySelector(".match-status span");
        if (matchStatusEl) matchStatusEl.innerText = "Trận đấu đang diễn ra...";

        const cancelForm = document.querySelector(".menu-items form[action='/game/cancel']");
        if (cancelForm) cancelForm.style.display = 'none';
    }

    const isGameReady = (window.WHITE_PLAYER !== '' && window.BLACK_PLAYER !== '') || window.isBotMode;
    if (isGameReady) {
        window.resetTimer('WHITE');
    }
});
