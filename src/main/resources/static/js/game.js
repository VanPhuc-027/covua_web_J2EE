document.addEventListener("DOMContentLoaded", function () {
    const config = window.CHESS_CONFIG || {};
    const gameId = window.CURRENT_GAME_ID || config.gameId || "default_id";
    const currentUsername = window.CURRENT_USERNAME || config.username || "guest";
    const whitePlayer = window.WHITE_PLAYER || "";
    const blackPlayer = window.BLACK_PLAYER || "";

    if (!window.CURRENT_GAME_ID && config.gameId) {
        window.CURRENT_GAME_ID = config.gameId;
    }
    if (!window.CURRENT_USERNAME && config.username) {
        window.CURRENT_USERNAME = config.username;
    }

    const isWhite = currentUsername === whitePlayer;
    const isBlack = currentUsername === blackPlayer;

    const isBotMode = sessionStorage.getItem("isBotMode") === "true";
    const botDepth = Number(sessionStorage.getItem("botDepth") || 10);
    const myColor = isWhite ? "WHITE" : (isBlack ? "BLACK" : (isBotMode ? "WHITE" : null));

    let selectedSquare = null;
    const squares = Array.from(document.querySelectorAll(".square"));
    const squareByPos = new Map();
    window.validMoves = window.validMoves || {};

    const sounds = {
    move: new Audio("/sounds/move-self.mp3"),
    capture: new Audio("/sounds/capture.mp3"),
    check: new Audio("/sounds/move-check.mp3"),
    castle: new Audio("/sounds/castle.mp3"),
    promote: new Audio("/sounds/promote.mp3"),
    notify: new Audio("/sounds/notify.mp3")
};

function isSoundEnabled() {
    return localStorage.getItem("chess_sound") !== "false";
}

function playAudio(audio) {
    if (!audio || !isSoundEnabled()) return;

    try {
        audio.currentTime = 0;
        audio.play().catch(function (err) {
            console.warn("Không phát được âm thanh:", err);
        });
    } catch (error) {
        console.error("Lỗi phát âm thanh:", error);
    }
}

function playMoveSound(isCheck, isPromote, isCapture, isCastle) {
    if (isCheck) return playAudio(sounds.check);
    if (isPromote) return playAudio(sounds.promote);
    if (isCapture) return playAudio(sounds.capture);
    if (isCastle) return playAudio(sounds.castle);
    playAudio(sounds.move);
}

    squares.forEach(function (square) {
        const row = square.dataset.row;
        const col = square.dataset.col;
        if (row != null && col != null) {
            squareByPos.set(`${row},${col}`, square);
        }
    });

    function isSoundEnabled() {
        return localStorage.getItem("chess_sound") !== "false";
    }

    function isHintsEnabled() {
        return localStorage.getItem("chess_hints") !== "false";
    }

    function getSquareByPosition(row, col) {
        return squareByPos.get(`${row},${col}`) || null;
    }

    function playAudio(audio) {
        if (!audio || !isSoundEnabled()) return;
        try {
            audio.currentTime = 0;
            audio.play().catch(function () {});
        } catch (error) {
            console.error("Lỗi phát âm thanh:", error);
        }
    }

    function playMoveSound(isCheck, isPromote, isCapture, isCastle) {
        if (isCheck) return playAudio(sounds.check);
        if (isPromote) return playAudio(sounds.promote);
        if (isCapture) return playAudio(sounds.capture);
        if (isCastle) return playAudio(sounds.castle);
        playAudio(sounds.move);
    }

    function getStatusSpan() {
        return document.querySelector(".match-status span") || document.querySelector(".match-status");
    }

    function clearHighlights() {
        squares.forEach(function (square) {
            square.classList.remove("selected", "valid-move", "valid-capture", "hint", "checked-king");
        });
    }

    function clearMoveHighlights() {
        squares.forEach(function (square) {
            square.classList.remove("selected", "valid-move", "valid-capture", "hint");
        });
    }

    function isPieceSquare(squareEl) {
        return !!(squareEl && squareEl.querySelector("img"));
    }

    function getSquarePieceColor(squareEl) {
        const img = squareEl ? squareEl.querySelector("img") : null;
        const src = img ? (img.getAttribute("src") || "").toLowerCase() : "";
        if (src.includes("white_")) return "WHITE";
        if (src.includes("black_")) return "BLACK";
        return null;
    }

    function renderBoardFromResponse(board) {
        const boxes = board && board.boxes;
        if (!Array.isArray(boxes) || boxes.length !== 8) return;

        for (let r = 0; r < 8; r += 1) {
            if (!Array.isArray(boxes[r]) || boxes[r].length !== 8) continue;

            for (let c = 0; c < 8; c += 1) {
                const square = squareByPos.get(`${r},${c}`);
                if (!square) continue;

                const piece = boxes[r][c] && boxes[r][c].piece ? boxes[r][c].piece : null;
                const existingImg = square.querySelector("img");

                if (!piece) {
                    if (existingImg) existingImg.remove();
                    continue;
                }

                if (existingImg) {
                    if (existingImg.getAttribute("src") !== piece.iconPath) {
                        existingImg.setAttribute("src", piece.iconPath);
                    }
                    existingImg.classList.add("chess-piece");
                } else {
                    const img = document.createElement("img");
                    img.src = piece.iconPath;
                    img.alt = "chess-piece";
                    img.classList.add("chess-piece");
                    square.appendChild(img);
                }
            }
        }
    }

    window.renderBoardFromResponse = renderBoardFromResponse;

    if (isBlack) {
        const boardEl = document.querySelector(".chessboard");
        if (boardEl) boardEl.classList.add("flipped");
    }

    async function highlightValidMoves(fromRow, fromCol) {
        clearMoveHighlights();

        const originSquare = getSquareByPosition(fromRow, fromCol);
        if (originSquare) {
            originSquare.classList.add("selected");
        }

        if (!isHintsEnabled()) return;

        let moves = window.validMoves[`${fromRow},${fromCol}`];

        if (!Array.isArray(moves)) {
            try {
                const response = await fetch(
                    `/api/game/${gameId}/valid-moves?row=${encodeURIComponent(fromRow)}&col=${encodeURIComponent(fromCol)}`
                );
                if (!response.ok) return;

                moves = await response.json();
                window.validMoves[`${fromRow},${fromCol}`] = moves;
            } catch (error) {
                console.error("Lỗi lấy valid moves:", error);
                return;
            }
        }

        if (!Array.isArray(moves)) return;

        moves.forEach(function (move) {
            const row = move[0];
            const col = move[1];
            const targetSquare = getSquareByPosition(row, col);

            if (!targetSquare) return;

            targetSquare.classList.add("hint");

            if (isPieceSquare(targetSquare)) {
                targetSquare.classList.add("valid-capture");
            } else {
                targetSquare.classList.add("valid-move");
            }
        });
    }

    function isCastlingMove(pieceImg, fromCol, toCol) {
        const src = pieceImg ? (pieceImg.getAttribute("src") || "").toLowerCase() : "";
        return src.includes("king") && Math.abs(Number(fromCol) - Number(toCol)) === 2;
    }

    async function movePiece(fromRow, fromCol, toRow, toCol, promotion) {
        if (!myColor) return false;

        const fromSquare = squareByPos.get(`${fromRow},${fromCol}`);
        const pieceImg = fromSquare ? fromSquare.querySelector("img") : null;
        if (!pieceImg) return false;

        const pieceSrc = (pieceImg.getAttribute("src") || "").toLowerCase();
        const isPawn = pieceSrc.includes("pawn");
        const isCastle = isCastlingMove(pieceImg, fromCol, toCol);
        const targetSquare = squareByPos.get(`${toRow},${toCol}`);

        let isCapture = !!(targetSquare && targetSquare.querySelector("img"));
        if (isPawn && Number(fromCol) !== Number(toCol) && !isCapture) {
            isCapture = true;
        }

        const isLastRank =
            (myColor === "WHITE" && Number(toRow) === 0) ||
            (myColor === "BLACK" && Number(toRow) === 7);

        if (isPawn && isLastRank && !promotion) {
            showPromotionModal(fromRow, fromCol, toRow, toCol);
            return false;
        }

        try {
            const response = await fetch(`/api/game/${gameId}/move`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    fromRow: Number(fromRow),
                    fromCol: Number(fromCol),
                    toRow: Number(toRow),
                    toCol: Number(toCol),
                    promotion: promotion || null
                })
            });

            const data = await response.json();

            if (!data || !data.success) {
                alert((data && data.message) || "Nước đi không hợp lệ");
                if (typeof window.fetchAndRenderBoard === "function") {
                    await window.fetchAndRenderBoard();
                }
                return false;
            }

            renderBoardFromResponse(data.board);
            playMoveSound(!!data.check, !!promotion, isCapture, isCastle);

            if (typeof window.handleCheckStatus === "function") {
                window.handleCheckStatus(data);
            }

            if (isBotMode && data.currentTurn === "BLACK") {
                setTimeout(function () {
                    fetch(`/api/game/${gameId}/bot-move?depth=${botDepth}`, { method: "POST" })
                        .catch(function (err) {
                            console.error("Lỗi gọi Bot:", err);
                        });
                }, 500);
            }

            return true;
        } catch (error) {
            console.error("Lỗi di chuyển quân cờ:", error);
            alert("Không thể kết nối tới máy chủ");
            if (typeof window.fetchAndRenderBoard === "function") {
                await window.fetchAndRenderBoard();
            }
            return false;
        }
    }

    function closeOverlay(overlay) {
        if (!overlay) return;
        overlay.style.opacity = "0";
        overlay.style.pointerEvents = "none";
        setTimeout(function () {
            overlay.style.display = "none";
        }, 300);
    }

    function showOverlay(overlay) {
        if (!overlay) return;
        overlay.style.display = "flex";
        void overlay.offsetWidth;
        overlay.style.opacity = "1";
        overlay.style.pointerEvents = "auto";
    }

    window.showModal = function (icon, title, message, type, confirmCallback, cancelCallback) {
        const overlay = document.getElementById("game-modal-overlay");
        if (!overlay) {
            if (message) alert(message);
            return;
        }

        const iconEl = document.getElementById("game-modal-icon");
        const titleEl = document.getElementById("game-modal-title");
        const messageEl = document.getElementById("game-modal-message");
        const confirmBtn = document.getElementById("game-modal-btn-confirm");
        const cancelBtn = document.getElementById("game-modal-btn-cancel");
        const okBtn = document.getElementById("game-modal-btn-ok");

        if (!iconEl || !titleEl || !messageEl || !confirmBtn || !cancelBtn || !okBtn) {
            alert(message || title || "Thông báo");
            return;
        }

        iconEl.innerText = icon || "";
        titleEl.innerText = title || "Thông báo";
        messageEl.innerHTML = message || "";

        const newConfirmBtn = confirmBtn.cloneNode(true);
        const newCancelBtn = cancelBtn.cloneNode(true);
        const newOkBtn = okBtn.cloneNode(true);

        confirmBtn.parentNode.replaceChild(newConfirmBtn, confirmBtn);
        cancelBtn.parentNode.replaceChild(newCancelBtn, cancelBtn);
        okBtn.parentNode.replaceChild(newOkBtn, okBtn);

        if (type === "confirm") {
            newConfirmBtn.style.display = "block";
            newCancelBtn.style.display = "block";
            newOkBtn.style.display = "none";

            newConfirmBtn.onclick = function () {
                closeOverlay(overlay);
                if (typeof confirmCallback === "function") confirmCallback();
            };

            newCancelBtn.onclick = function () {
                closeOverlay(overlay);
                if (typeof cancelCallback === "function") cancelCallback();
            };
        } else {
            newConfirmBtn.style.display = "none";
            newCancelBtn.style.display = "none";
            newOkBtn.style.display = "block";

            newOkBtn.onclick = function () {
                closeOverlay(overlay);
                if (typeof confirmCallback === "function") confirmCallback();
            };
        }

        showOverlay(overlay);
    };

    window.scheduleReturnToLobby = function (delaySec) {
        let remaining = Number(delaySec) || 5;
        const messageEl = document.getElementById("game-modal-message");
        const baseMessage = messageEl ? messageEl.innerHTML : "";

        const timer = setInterval(function () {
            remaining -= 1;
            if (messageEl) {
                messageEl.innerHTML =
                    `${baseMessage}<br><br><span style="color:#aaa;font-size:13px;">` +
                    `Chuyển về sảnh sau <b style="color:#fff">${remaining}s</b>...</span>`;
            }

            if (remaining <= 0) {
                clearInterval(timer);
                window.location.href = "/";
            }
        }, 1000);
    };

    function showPromotionModal(fromRow, fromCol, toRow, toCol) {
        if (!myColor) return;

        const overlay = document.getElementById("promotion-modal-overlay");
        if (!overlay) return;

        const color = myColor.toLowerCase();
        const mapping = [
            ["promo-img-queen", "queen"],
            ["promo-img-rook", "rook"],
            ["promo-img-bishop", "bishop"],
            ["promo-img-knight", "knight"]
        ];

        mapping.forEach(function (entry) {
            const img = document.getElementById(entry[0]);
            if (img) img.src = `/img/${color}_${entry[1]}.png`;
        });

        overlay.style.display = "flex";

        document.querySelectorAll(".promo-btn").forEach(function (button) {
            button.onclick = async function () {
                const piece = button.dataset.piece;
                overlay.style.display = "none";
                await movePiece(fromRow, fromCol, toRow, toCol, piece);
                selectedSquare = null;
                clearMoveHighlights();
            };
        });
    }

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

    window.fetchAndRenderBoard = async function () {
        try {
            const response = await fetch(`/api/game/${gameId}/board`);
            if (!response.ok) return;

            const boardData = await response.json();
            renderBoardFromResponse(boardData);
        } catch (error) {
            console.error("Lỗi tải bàn cờ:", error);

            try {
                const res = await fetch(`/api/game/${gameId}/state`);
                if (!res.ok) return;

                const data = await res.json();
                if (data.board) renderBoardFromResponse(data.board);
                if (typeof window.handleCheckStatus === "function") {
                    window.handleCheckStatus(data);
                }
            } catch (e) {
                console.error("Lỗi khi kéo trạng thái game:", e);
            }
        }
    };

    async function loadChatHistory() {
        const chatMessages = document.getElementById("chat-messages");
        if (!chatMessages) return;

        try {
            const response = await fetch(`/api/game/${gameId}/chat-history`);
            if (!response.ok) return;

            const history = await response.json();
            if (!Array.isArray(history)) return;

            history.forEach(function (message) {
                if (typeof window.renderChatMessage === "function") {
                    window.renderChatMessage(message);
                }
            });
        } catch (error) {
            console.error("Lỗi tải lịch sử chat:", error);
        }
    }

    function sendChatMessage() {
        const input = document.getElementById("chat-input");
        if (!input) return;

        const content = input.value.trim();
        if (!content || !window.stompClient) return;

        window.stompClient.send(
            `/app/chat/${window.CURRENT_GAME_ID}`,
            {},
            JSON.stringify({
                gameId: window.CURRENT_GAME_ID,
                sender: currentUsername,
                content: content,
                type: "CHAT"
            })
        );

        input.value = "";
    }

    window.renderChatMessage = function (chatMessage) {
        const container = document.getElementById("chat-messages");
        if (!container || !chatMessage) return;

        if (chatMessage.type === "SYSTEM") {
            const systemLine = document.createElement("div");
            systemLine.style.cssText = "text-align:center;font-size:11px;color:#777;margin:5px 0;";
            systemLine.innerText = chatMessage.content || "";
            container.appendChild(systemLine);
        } else {
            const isMine = chatMessage.sender === currentUsername;
            const wrapper = document.createElement("div");
            wrapper.className = `message-wrapper ${isMine ? "me" : "them"}`;
            wrapper.innerHTML =
                `<div class="chat-sender">${isMine ? "BẠN" : (chatMessage.sender || "Người chơi")}</div>` +
                `<div class="chat-bubble">${chatMessage.content || ""}</div>`;
            container.appendChild(wrapper);
        }

        container.scrollTop = container.scrollHeight;
    };

    window.handleCheckStatus = function (payload) {
        squares.forEach(function (square) {
            square.classList.remove("checked-king");
        });

        const statusSpan = getStatusSpan();
        if (!payload) return;

        if (payload.moveHistory) {
            renderMoveHistory(payload.moveHistory);
        }

        if (payload.validMoves) {
            window.validMoves = payload.validMoves;
        }

        if (payload.action) {
            if (payload.message && typeof window.renderChatMessage === "function") {
                window.renderChatMessage({
                    type: "SYSTEM",
                    content: payload.message
                });
            }

            if (payload.action === "OFFER_DRAW") {
                if (myColor && payload.actionPlayer !== myColor) {
                    window.pauseTimer();
                    window.showModal(
                        "🤝",
                        "Yêu cầu Cầu hòa",
                        payload.message || "Đối thủ muốn cầu hòa. Bạn có đồng ý không?",
                        "confirm",
                        function () {
                            window.sendAction("ACCEPT_DRAW");
                        },
                        function () {
                            window.sendAction("DECLINE_DRAW");
                            window.resumeTimer();
                        }
                    );
                }
                return;
            }

            if (["RESIGN", "TIMEOUT", "ACCEPT_DRAW"].includes(payload.action)) {
                if (statusSpan) {
                    statusSpan.innerHTML = `<span style="color:#ff4d4d;font-weight:bold;">${payload.message || ""}</span>`;
                }

                playAudio(sounds.notify);

                window.showModal(
                    payload.action === "TIMEOUT" ? "⏰" : (payload.action === "RESIGN" ? "🏳️" : "🤝"),
                    "Kết Thúc Trận Đấu",
                    payload.message || "Trận đấu đã kết thúc",
                    "alert",
                    function () {
                        window.location.href = "/";
                    }
                );

                window.scheduleReturnToLobby(5);

                if (window.timerInterval) clearInterval(window.timerInterval);
                clearMoveHighlights();
                selectedSquare = null;
                return;
            }
        }

        if (payload.currentTurn) {
            window.resetTimer(payload.currentTurn);

            if (statusSpan && !payload.check && !payload.checkmate && !payload.action) {
                const turnName = payload.currentTurn === "WHITE" ? "TRẮNG" : "ĐEN";
                statusSpan.innerHTML =
                    `Lượt của: <b style="color: var(--primary-color); text-shadow: 0 0 5px rgba(255,255,255,0.2)">${turnName}</b>`;
            }
        }

        if (payload.checkmate) {
            const resultMsg = `CHIẾU HẾT! Quân ${payload.winner === "WHITE" ? "Trắng" : "Đen"} giành chiến thắng!`;

            if (statusSpan) {
                statusSpan.innerHTML = `<span style="color:#ff4d4d;font-weight:bold;">${resultMsg}</span>`;
            }

            if (window.timerInterval) clearInterval(window.timerInterval);
            playAudio(sounds.notify);

            window.showModal("🏆", "Chiếu hết!", resultMsg, "alert", function () {
                window.location.href = "/";
            });

            window.scheduleReturnToLobby(5);

            if (typeof window.renderChatMessage === "function") {
                window.renderChatMessage({ type: "SYSTEM", content: resultMsg });
            }
            return;
        }

        if (payload.check) {
            if (statusSpan) {
                statusSpan.innerHTML =
                    '<span style="color:#ffaa00;font-weight:bold;animation:pulse-text 1s infinite alternate;">ĐANG BỊ CHIẾU TƯỚNG!</span>';
            }

            if (payload.currentTurn === myColor) {
                playAudio(sounds.check);
            }

            if (payload.kingRow !== undefined && payload.kingCol !== undefined) {
                const checkedSquare = squareByPos.get(`${payload.kingRow},${payload.kingCol}`);
                if (checkedSquare) checkedSquare.classList.add("checked-king");
            }
            return;
        }

        if (statusSpan && !payload.action) {
            statusSpan.textContent = "Trận đấu đang diễn ra...";
        }
    };

    const DEFAULT_TIME = 180;
    window.timerSeconds = DEFAULT_TIME;
    window.timerInterval = null;
    window.currentTurn = "WHITE";
    window.isPaused = false;

    window.pauseTimer = function () {
        window.isPaused = true;
    };

    window.resumeTimer = function () {
        window.isPaused = false;
    };

    function updateTimerUI() {
        const opponentTimer = document.getElementById("timer-opponent");
        const yourTimer = document.getElementById("timer-you");
        if (!opponentTimer || !yourTimer) return;

        opponentTimer.innerText = DEFAULT_TIME + "s";
        yourTimer.innerText = DEFAULT_TIME + "s";
        opponentTimer.style.color = "";
        yourTimer.style.color = "";

        if (window.currentTurn === myColor) {
            yourTimer.innerText = window.timerSeconds + "s";
            if (window.timerSeconds <= 5) yourTimer.style.color = "#ff4d4d";
        } else {
            opponentTimer.innerText = window.timerSeconds + "s";
            if (window.timerSeconds <= 5) opponentTimer.style.color = "#ff4d4d";
        }
    }

    window.resetTimer = function (newTurn) {
        if (window.timerInterval) clearInterval(window.timerInterval);

        window.currentTurn = newTurn || window.currentTurn;
        window.timerSeconds = DEFAULT_TIME;
        window.isPaused = false;
        updateTimerUI();

        window.timerInterval = setInterval(function () {
            if (window.isPaused) return;

            window.timerSeconds -= 1;
            updateTimerUI();

            if (window.timerSeconds <= 0) {
                clearInterval(window.timerInterval);
                if (myColor) {
                    window.sendAction("TIMEOUT");
                }
            }
        }, 1000);
    };

    window.sendAction = function (actionType) {
        if (!myColor) return;

        fetch(`/api/game/${gameId}/action`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ action: actionType })
        }).catch(function (error) {
            console.error("Lỗi gửi hành động:", error);
        });
    };

    squares.forEach(function (square) {
        square.addEventListener("click", async function () {
            const row = square.dataset.row;
            const col = square.dataset.col;

            if (!selectedSquare) {
                if (!isPieceSquare(square)) return;

                const pieceColor = getSquarePieceColor(square);
                if (myColor && pieceColor !== myColor) return;

                selectedSquare = { row: row, col: col };
                await highlightValidMoves(row, col);
                return;
            }

            if (selectedSquare.row === row && selectedSquare.col === col) {
                clearMoveHighlights();
                selectedSquare = null;
                return;
            }

            const clickedOwnPiece = isPieceSquare(square) && getSquarePieceColor(square) === myColor;

            if (clickedOwnPiece) {
                selectedSquare = { row: row, col: col };
                await highlightValidMoves(row, col);
                return;
            }

            if (isHintsEnabled()) {
                const canMove =
                    square.classList.contains("valid-move") ||
                    square.classList.contains("valid-capture") ||
                    square.classList.contains("hint");

                if (!canMove) return;
            }

            const moved = await movePiece(selectedSquare.row, selectedSquare.col, row, col);
            const promotionOverlay = document.getElementById("promotion-modal-overlay");

            if (moved && (!promotionOverlay || promotionOverlay.style.display !== "flex")) {
                clearMoveHighlights();
                selectedSquare = null;
            }
        });
    });

    const sendChatButton = document.getElementById("btn-send-chat");
    if (sendChatButton) {
        sendChatButton.addEventListener("click", sendChatMessage);
    }

    const chatInput = document.getElementById("chat-input");
    if (chatInput) {
        chatInput.addEventListener("keypress", function (event) {
            if (event.key === "Enter") {
                sendChatMessage();
            }
        });
    }

    const resignButton = document.getElementById("btn-resign");
    if (resignButton) {
        resignButton.onclick = function () {
            window.showModal(
                "🏳️",
                "Đầu hàng",
                "Bạn có chắc chắn muốn đầu hàng không?",
                "confirm",
                function () {
                    window.sendAction("RESIGN");
                }
            );
        };
    }

    const drawButton = document.getElementById("btn-draw");
    if (drawButton) {
        drawButton.onclick = function () {
            window.sendAction("OFFER_DRAW");
            window.showModal("📩", "Đã gửi", "Đã gửi lời mời cầu hòa tới đối thủ.", "alert");
        };
    }

    loadChatHistory();

    if (isBotMode) {
        const waitingOverlay = document.querySelector(".waiting-overlay");
        if (waitingOverlay) waitingOverlay.style.display = "none";

        const oppNameEl = document.querySelector(".player.opponent .details .name");
        if (oppNameEl) {
            const diff = botDepth <= 6 ? "Dễ" : (botDepth <= 10 ? "Trung Bình" : "Khó");
            oppNameEl.innerText = "BOT_FURINA (" + diff + ")";
        }

        const matchStatusEl = document.querySelector(".match-status span");
        if (matchStatusEl) matchStatusEl.innerText = "Trận đấu đang diễn ra...";

        const cancelForm = document.querySelector(".menu-items form[action='/game/cancel']");
        if (cancelForm) cancelForm.style.display = "none";

        let gameActions = document.querySelector(".game-actions");
        if (!gameActions) {
            gameActions = document.createElement("div");
            gameActions.className = "game-actions";
            gameActions.style.cssText =
                "display:flex;gap:10px;padding:15px;border-bottom:1px solid rgba(255,255,255,0.1);";

            gameActions.innerHTML = `
                <button id="btn-draw-bot" style="flex:1; padding:10px; border-radius:8px; border:none; background:#607d8b; color:white; cursor:pointer; font-weight:bold; font-family:inherit; font-size:14px;">🤝 Cầu hòa</button>
                <button id="btn-resign-bot" style="flex:1; padding:10px; border-radius:8px; border:none; background:#f44336; color:white; cursor:pointer; font-weight:bold; font-family:inherit; font-size:14px;">🏳️ Đầu hàng</button>
            `;

            const rightPanel = document.querySelector(".right-panel");
            const moveHistory = document.querySelector(".move-history");

            if (rightPanel && moveHistory) {
                rightPanel.insertBefore(gameActions, moveHistory);
            }

            const resignBotBtn = document.getElementById("btn-resign-bot");
            if (resignBotBtn) {
                resignBotBtn.onclick = function () {
                    window.showModal(
                        "🏳️",
                        "Đầu hàng",
                        "Bạn chắc chắn muốn đầu hàng em Bot này?",
                        "confirm",
                        function () {
                            window.sendAction("RESIGN");
                        }
                    );
                };
            }

            const drawBotBtn = document.getElementById("btn-draw-bot");
            if (drawBotBtn) {
                drawBotBtn.onclick = function () {
                    window.showModal(
                        "🤖",
                        "Cầu hòa",
                        "Đánh với ta không có khái niệm cầu hòa đâu, ráng mà đánh chiến thắng đi!",
                        "alert"
                    );
                };
            }
        }
    }

    const isGameReady = (whitePlayer !== "" && blackPlayer !== "") || isBotMode;

    if (isGameReady) {
        window.resetTimer("WHITE");
    } else {
        const oppTimer = document.getElementById("timer-opponent");
        const youTimer = document.getElementById("timer-you");
        if (oppTimer) oppTimer.innerText = DEFAULT_TIME + "s";
        if (youTimer) youTimer.innerText = DEFAULT_TIME + "s";
    }
});