document.addEventListener("DOMContentLoaded", function () {
    const gameId = window.CURRENT_GAME_ID;
    const isWhite = window.CURRENT_USERNAME === window.WHITE_PLAYER;
    const isBlack = window.CURRENT_USERNAME === window.BLACK_PLAYER;

    // Đọc xem có phải đang đấu Máy không
    const isBotMode = sessionStorage.getItem("isBotMode") === "true";
    const botDepth = sessionStorage.getItem("botDepth") || 10;

    // Mặc định đấu máy thì mình cầm Trắng
    const myColor = isWhite ? 'WHITE' : (isBlack ? 'BLACK' : (isBotMode ? 'WHITE' : null));

    let selectedSquare = null;
    const squares = Array.from(document.querySelectorAll(".square"));
    const squareByPos = new Map();

    const soundMove = new Audio('/sounds/move-self.mp3');
    const soundCapture = new Audio('/sounds/capture.mp3');
    const soundCheck = new Audio('/sounds/move-check.mp3');
    const soundCastle = new Audio('/sounds/castle.mp3');
    const soundPromote = new Audio('/sounds/promote.mp3');
    const soundNotify = new Audio('/sounds/notify.mp3');

    function playMoveSound(isCheck, isPromote, isCapture, isCastle) {
        try {
            if (isCheck) {
                soundCheck.play().catch(e => console.log(e));
            } else if (isPromote) {
                soundPromote.play().catch(e => console.log(e));
            } else if (isCapture) {
                soundCapture.play().catch(e => console.log(e));
            } else if (isCastle) {
                soundCastle.play().catch(e => console.log(e));
            } else {
                soundMove.play().catch(e => console.log(e));
            }
        } catch (err) {
            console.error("Lỗi phát âm thanh:", err);
        }
    }

    if (isBlack) {
        const boardEl = document.querySelector('.chessboard');
        if (boardEl) boardEl.classList.add('flipped');
    }

    window.showModal = function(icon, title, message, type, confirmCallback) {
        const overlay = document.getElementById("game-modal-overlay");
        if (!overlay) return;

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

        const closeModal = () => {
            overlay.style.opacity = "0";
            overlay.style.pointerEvents = "none";
            setTimeout(() => overlay.style.display = "none", 300);
        };

        if (type === "confirm") {
            newConfirm.style.display = "block";
            newCancel.style.display = "block";
            newOk.style.display = "none";

            newConfirm.onclick = () => { closeModal(); if(confirmCallback) confirmCallback(); };
            newCancel.onclick = () => { closeModal(); };
        } else {
            newConfirm.style.display = "none";
            newCancel.style.display = "none";
            newOk.style.display = "block";

            newOk.onclick = () => { closeModal(); if(confirmCallback) confirmCallback(); };
        }

        overlay.style.display = "flex";
        void overlay.offsetWidth;
        overlay.style.opacity = "1";
        overlay.style.pointerEvents = "auto";
    };

    window.scheduleReturnToLobby = function(delaySec) {
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

    squares.forEach(sq => {
        const r = sq.dataset.row;
        const c = sq.dataset.col;
        if (r != null && c != null) {
            squareByPos.set(`${r},${c}`, sq);
        }
    });

    function clearHighlights() {
        squares.forEach(s => {
            s.classList.remove("selected", "valid-move", "valid-capture");
        });
    }

    function isPieceSquare(squareEl) {
        return !!squareEl.querySelector("img");
    }

    function renderBoardFromResponse(board) {
        const boxes = board?.boxes;
        if (!Array.isArray(boxes) || boxes.length !== 8) return;

        for (let r = 0; r < 8; r++) {
            const row = boxes[r];
            if (!Array.isArray(row) || row.length !== 8) continue;

            for (let c = 0; c < 8; c++) {
                const spot = row[c];
                const square = squareByPos.get(`${r},${c}`);
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
    }

    window.renderBoardFromResponse = renderBoardFromResponse;

    async function highlightValidMoves(fromRow, fromCol) {
        squares.forEach(s => {
            s.classList.remove("valid-move", "valid-capture");
        });

        let moves = window.validMoves ? window.validMoves[`${fromRow},${fromCol}`] : null;

        if (!Array.isArray(moves)) {
            try {
                const res = await fetch(`/api/game/${gameId}/valid-moves?row=${fromRow}&col=${fromCol}`);
                if (res.ok) {
                    moves = await res.json();
                }
            } catch (e) {
                console.error("Failed to fetch valid moves from API:", e);
                return;
            }
        }

        if (!Array.isArray(moves)) return;

        if (selectedSquare == null || selectedSquare.row != fromRow || selectedSquare.col != fromCol) {
            return;
        }

        squares.forEach(s => {
            s.classList.remove("valid-move", "valid-capture");
        });

        moves.forEach(move => {
            const r = move?.[0];
            const c = move?.[1];
            if (typeof r !== "number" || typeof c !== "number") return;

            const target = squareByPos.get(`${r},${c}`);
            if (!target) return;

            if (isPieceSquare(target)) {
                target.classList.add("valid-capture");
            } else {
                target.classList.add("valid-move");
            }
        });
    }

    async function movePiece(fromRow, fromCol, toRow, toCol, promotion = null) {
        if (!myColor) return false;

        const square = squareByPos.get(`${fromRow},${fromCol}`);
        const img = square?.querySelector("img");
        const isPawn = img && img.getAttribute("src").toLowerCase().includes("pawn");
        const targetSquare = squareByPos.get(`${toRow},${toCol}`);
        let isCapture = targetSquare && targetSquare.querySelector("img") !== null;
        if (isPawn && fromCol !== toCol && !isCapture) {
            isCapture = true;
        }

        const isLastRank = (myColor === "WHITE" && parseInt(toRow) === 0) || (myColor === "BLACK" && parseInt(toRow) === 7);
        if (isPawn && isLastRank && !promotion) {
            showPromotionModal(fromRow, fromCol, toRow, toCol);
            return false;
        }

        const capturedImg = targetSquare?.querySelector("img");
        if (capturedImg) capturedImg.remove();
        if (img && targetSquare) targetSquare.appendChild(img);

        try {
            const res = await fetch(`/api/game/${gameId}/move`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    fromRow: parseInt(fromRow),
                    fromCol: parseInt(fromCol),
                    toRow: parseInt(toRow),
                    toCol: parseInt(toCol),
                    promotion: promotion
                })
            });

            const data = await res.json();
            if (data?.success) {
                console.log("--- KẾT QUẢ TỪ BACKEND TRẢ VỀ ---", data);
                renderBoardFromResponse(data.board);
                playMoveSound(false, promotion !== null, isCapture, false);

                if (typeof window.handleCheckStatus === "function") {
                    window.handleCheckStatus(data);
                }

                if (isBotMode && data.currentTurn === "BLACK") {
                    setTimeout(() => {
                        fetch(`/api/game/${gameId}/bot-move?depth=${botDepth}`, { method: "POST" })
                            .catch(err => console.error("Lỗi gọi Bot:", err));
                    }, 500);
                }
                return true;
            }
            window.fetchAndRenderBoard();
            return false;
        } catch (error) {
            console.error("Error:", error);
            window.fetchAndRenderBoard();
            return false;
        }
    }

    function showPromotionModal(fR, fC, tR, tC) {
        if (!myColor) return;
        const overlay = document.getElementById("promotion-modal-overlay");
        const color = myColor.toLowerCase();

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
                await movePiece(fR, fC, tR, tC, piece);
                selectedSquare = null;
                clearHighlights();
            };
        });
    }

    squares.forEach(square => {
        square.addEventListener("click", async () => {
            const row = square.dataset.row;
            const col = square.dataset.col;

            if (!selectedSquare) {
                if (!isPieceSquare(square)) return;
                if (myColor) {
                    const img = square.querySelector("img");
                    const pieceColor = img && img.getAttribute("src").includes("white_") ? "WHITE" : "BLACK";
                    if (pieceColor !== myColor) return;
                }
                selectedSquare = {row, col};
                square.classList.add("selected");
                await highlightValidMoves(row, col);
            } else {
                if (selectedSquare.row === row && selectedSquare.col === col) {
                    clearHighlights();
                    selectedSquare = null;
                    return;
                }
                if (!square.classList.contains("valid-move") && !square.classList.contains("valid-capture")) {
                    if (isPieceSquare(square)) {
                        if (myColor) {
                            const img = square.querySelector("img");
                            const pieceColor = img && img.getAttribute("src").includes("white_") ? "WHITE" : "BLACK";
                            if (pieceColor !== myColor) return;
                        }
                        clearHighlights();
                        selectedSquare = {row, col};
                        square.classList.add("selected");
                        await highlightValidMoves(row, col);
                    }
                    return;
                }
                await movePiece(selectedSquare.row, selectedSquare.col, row, col);
                if (document.getElementById("promotion-modal-overlay").style.display !== "flex") {
                    clearHighlights();
                    selectedSquare = null;
                }
            }
        });
    });

    window.fetchAndRenderBoard = async function() {
        try {
            const res = await fetch(`/api/game/${gameId}/state`);
            if (!res.ok) return;
            const data = await res.json();
            if (data.board) {
                renderBoardFromResponse(data.board);
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

    window.renderChatMessage = function(chatMessage) {
        const messageContainer = document.getElementById("chat-messages");
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

    document.getElementById("btn-send-chat").addEventListener("click", sendChatMessage);
    document.getElementById("chat-input").addEventListener("keypress", function (e) {
        if (e.key === "Enter") sendChatMessage();
    });
    loadChatHistory();

    function renderMoveHistory(history) {
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
    }

    window.handleCheckStatus = function(payload) {
        document.querySelectorAll(".square.checked-king").forEach(el => el.classList.remove("checked-king"));
        const statusSpan = document.querySelector(".match-status span");

        if (payload.moveHistory) {
            renderMoveHistory(payload.moveHistory);
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
                if (myColor && payload.actionPlayer !== myColor) {
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
                soundNotify.play().catch(e => {});
                window.showModal(icon, "Kết thúc trận đấu", payload.message, "alert", () => window.location.href = "/");
                window.scheduleReturnToLobby(5);
                if (window.timerInterval) clearInterval(window.timerInterval);
                clearHighlights();
                selectedSquare = null;
                return;
            }
        }

        if (payload.currentTurn) {
            window.resetTimer(payload.currentTurn);
            if (statusSpan && !payload.check && !payload.checkmate && !payload.action) {
                const turnName = payload.currentTurn === 'WHITE' ? 'TRẮNG' : 'ĐEN';
                const turnColor = payload.currentTurn === 'WHITE' ? '#fff' : '#aaa';
                statusSpan.innerHTML = `Lượt của: <b style="color: var(--primary-color); text-shadow: 0 0 5px rgba(255,255,255,0.2)">${turnName}</b>`;
            }
        }

        if (payload.checkmate) {
            const resultMsg = `CHIẾU HẾT! Quân ${payload.winner === 'WHITE' ? 'Trắng' : 'Đen'} giành chiến thắng!`;
            if (statusSpan) statusSpan.innerHTML = `<span style="color: #ff4d4d; font-weight: bold;">${resultMsg}</span>`;
            if (window.timerInterval) clearInterval(window.timerInterval);
            soundNotify.play().catch(e => {});
            window.showModal("🏆", "Chiếu hết!", resultMsg, "alert", () => window.location.href = "/");
            window.scheduleReturnToLobby(5);

            if (typeof window.renderChatMessage === 'function') {
                window.renderChatMessage({ type: 'SYSTEM', content: resultMsg });
            }
        } else if (payload.check) {
            if (statusSpan) statusSpan.innerHTML = `<span style="color: #ffaa00; font-weight: bold; animation: pulse-text 1s infinite alternate;">ĐANG BỊ CHIẾU TƯỚNG!</span>`;
            if (payload.currentTurn == myColor) {
                soundCheck.play().catch(e=>{});
            }
            if (payload.kingRow !== undefined && payload.kingCol !== undefined) {
                const checkedKingSquare = squareByPos.get(`${payload.kingRow},${payload.kingCol}`);
                if (checkedKingSquare) checkedKingSquare.classList.add("checked-king");
            }
        }
    };

    // GIỮ NGUYÊN CODE CỦA BẠN ÔNG
    window.timerSeconds = 180;
    window.timerInterval = null;
    window.currentTurn = 'WHITE';
    window.isPaused = false;

    window.pauseTimer = function() {
        window.isPaused = true;
    };

    window.resumeTimer = function() {
        window.isPaused = false;
    };

    window.resetTimer = function(newTurn) {
        if (window.timerInterval) clearInterval(window.timerInterval);
        window.currentTurn = newTurn || window.currentTurn;
        window.timerSeconds = 30; // Bug của bạn ông, tui giữ nguyên
        window.isPaused = false;
        updateTimerUI();

        window.timerInterval = setInterval(() => {
            if (window.isPaused) return;

            window.timerSeconds--;
            updateTimerUI();

            if (window.timerSeconds <= 0) {
                clearInterval(window.timerInterval);
                if (myColor && (window.currentTurn === myColor || true)) {
                    window.sendAction("TIMEOUT");
                }
            }
        }, 1000);
    };

    function updateTimerUI() {
        const oppTimer = document.getElementById('timer-opponent');
        const youTimer = document.getElementById('timer-you');
        if (!oppTimer || !youTimer) return;

        oppTimer.innerText = '30s'; // Giữ nguyên
        youTimer.innerText = '30s'; // Giữ nguyên

        if (window.currentTurn === myColor) {
            youTimer.innerText = window.timerSeconds + 's';
            if (window.timerSeconds <= 5) youTimer.style.color = '#ff4d4d';
            else youTimer.style.color = '';
        } else {
            oppTimer.innerText = window.timerSeconds + 's';
            if (window.timerSeconds <= 5) oppTimer.style.color = '#ff4d4d';
            else oppTimer.style.color = '';
        }
    }

    window.sendAction = function(actionType) {
        if (!myColor) {
            console.warn("Chỉ người chơi mới có thể thực hiện hành động này.");
            return;
        }
        fetch(`/api/game/${gameId}/action`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ action: actionType })
        }).catch(e => console.error("Lỗi khi gửi hành động:", e));
    };

    const resignBtn = document.getElementById('btn-resign');
    if (resignBtn) {
        resignBtn.onclick = () => {
            window.showModal("🏳️", "Đầu hàng", "Bạn có chắc chắn muốn đầu hàng không?", "confirm", () => {
                window.sendAction("RESIGN");
            });
        };
    }

    const drawBtn = document.getElementById('btn-draw');
    if (drawBtn) {
        drawBtn.onclick = () => {
            window.sendAction("OFFER_DRAW");
            window.showModal("📩", "Đã Gửi", "Đã gửi lời mời cầu hòa tới đối thủ.", "alert");
        };
    }

    // GIAO DIỆN CHO BOT
    if (isBotMode) {
        const waitingOverlay = document.querySelector(".waiting-overlay");
        if (waitingOverlay) waitingOverlay.style.display = "none";

        const oppNameEl = document.querySelector(".player.opponent .details .name");
        if (oppNameEl) {
            let diff = botDepth <= 6 ? "Dễ" : (botDepth <= 10 ? "Trung Bình" : "Khó");
            oppNameEl.innerText = "FURINA (" + diff + ")";
        }

        const matchStatusEl = document.querySelector(".match-status span");
        if (matchStatusEl) matchStatusEl.innerText = "Trận đấu đang diễn ra...";

        const cancelForm = document.querySelector(".menu-items form[action='/game/cancel']");
        if (cancelForm) cancelForm.style.display = 'none';

        let gameActions = document.querySelector(".game-actions");
        if (!gameActions) {
            gameActions = document.createElement("div");
            gameActions.className = "game-actions";
            gameActions.style.cssText = "display: flex; gap: 10px; padding: 15px; border-bottom: 1px solid rgba(255,255,255,0.1);";
            gameActions.innerHTML = `
                <button id="btn-draw" style="flex:1; padding: 10px; border-radius: 8px; border: none; background: #607d8b; color: white; cursor: pointer; font-weight: bold; font-family: inherit; font-size: 14px;">🤝 Cầu hòa</button>
                <button id="btn-resign" style="flex:1; padding: 10px; border-radius: 8px; border: none; background: #f44336; color: white; cursor: pointer; font-weight: bold; font-family: inherit; font-size: 14px;">🏳️ Đầu hàng</button>
            `;
            const rightPanel = document.querySelector(".right-panel");
            const moveHistory = document.querySelector(".move-history");
            rightPanel.insertBefore(gameActions, moveHistory);

            document.getElementById('btn-resign').onclick = () => {
                window.showModal("🏳️", "Đầu hàng", "Bạn chắc chắn muốn đầu hàng em Bot này?", "confirm", () => window.sendAction("RESIGN"));
            };
            document.getElementById('btn-draw').onclick = () => {
                window.showModal("🤖", "Cầu hòa", "Đánh với Ta không có khái niệm cầu hòa đâu, ráng mà dánh chiến thắng đi!", "alert");
            };
        }
    }

    window.resetTimer('WHITE');
});