document.addEventListener("DOMContentLoaded", function () {
    const gameId = window.CURRENT_GAME_ID; // Lấy ID phòng từ HTML
    const isWhite = window.CURRENT_USERNAME === window.WHITE_PLAYER;
    const isBlack = window.CURRENT_USERNAME === window.BLACK_PLAYER;
    const myColor = isWhite ? 'WHITE' : (isBlack ? 'BLACK' : null);

    let selectedSquare = null;
    const squares = Array.from(document.querySelectorAll(".square"));
    const squareByPos = new Map();
    
    // Đảo ngược bàn cờ nếu người chơi là quân đen
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
        // Force reflow
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

    // Expose for WebSocket updates (socket.js).
    window.renderBoardFromResponse = renderBoardFromResponse;

    async function highlightValidMoves(fromRow, fromCol) {
        squares.forEach(s => {
            s.classList.remove("valid-move", "valid-capture");
        });

        try {
            // Đã ghép gameId vào link
            const res = await fetch(`/api/game/${gameId}/valid-moves?row=${encodeURIComponent(fromRow)}&col=${encodeURIComponent(fromCol)}`);
            const moves = await res.json();

            if (!Array.isArray(moves)) return;

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
        } catch (e) {
            console.error("Failed to fetch valid moves:", e);
        }
    }

    async function movePiece(fromRow, fromCol, toRow, toCol) {
        try {
            // Đã ghép gameId vào link
            const res = await fetch(`/api/game/${gameId}/move`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    fromRow: parseInt(fromRow),
                    fromCol: parseInt(fromCol),
                    toRow: parseInt(toRow),
                    toCol: parseInt(toCol)
                })
            });

            const data = await res.json();
            console.log("Server response:", data);

            if (data?.success) {
                renderBoardFromResponse(data.board);
                if (typeof window.handleCheckStatus === "function") {
                    window.handleCheckStatus(data);
                }
                return true;
            }

            alert(data?.message ?? "Move failed");
            return false;
        } catch (error) {
            console.error("Error:", error);
            alert("Network error");
            return false;
        }
    }

    squares.forEach(square => {
        square.addEventListener("click", async () => {
            const row = square.dataset.row;
            const col = square.dataset.col;

            console.log("Clicked:", row, col);

            if (!selectedSquare) {
                if (!isPieceSquare(square)) return;
                
                if (myColor) {
                    const img = square.querySelector("img");
                    const pieceColor = img && img.getAttribute("src").includes("white_") ? "WHITE" : "BLACK";
                    if (pieceColor !== myColor) {
                        return; // Không được chọn quân của đối phương
                    }
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
                            if (pieceColor !== myColor) {
                                return; // Không được chọn quân của đối phương
                            }
                        }

                        clearHighlights();
                        selectedSquare = {row, col};
                        square.classList.add("selected");
                        await highlightValidMoves(row, col);
                    }
                    return;
                }

                await movePiece(selectedSquare.row, selectedSquare.col, row, col);
                clearHighlights();
                selectedSquare = null;
            }
        });
    });

    window.fetchAndRenderBoard = async function() {
        try {
            const res = await fetch(`/api/game/${gameId}/board`);
            if (!res.ok) return;
            const boardData = await res.json();
            renderBoardFromResponse(boardData);
        } catch (e) {
            console.error("Lỗi khi kéo bàn cờ mới:", e);
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
            } else {
                console.error("Lỗi khi tải lịch sử chat, mã trạng thái:", response.status);
            }
        } catch (error) {
            console.error("Lỗi kết nối khi tải lịch sử chat:", error);
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
            wrapper.innerHTML = `
            <div class="chat-sender">${isMe ? 'BẠN' : chatMessage.sender}</div>
            <div class="chat-bubble">${chatMessage.content}</div>
        `;
            messageContainer.appendChild(wrapper);
        }
        messageContainer.scrollTop = messageContainer.scrollHeight;
    };

    document.getElementById("btn-send-chat").addEventListener("click", sendChatMessage);

    document.getElementById("chat-input").addEventListener("keypress", function (e) {
        if (e.key === "Enter") {
            sendChatMessage();
        }
    });
    loadChatHistory();

    window.handleCheckStatus = function(payload) {
        // Clear old check highlights
        document.querySelectorAll(".square.checked-king").forEach(el => {
            el.classList.remove("checked-king");
        });

        const statusSpan = document.querySelector(".match-status span");

        if (payload.action) {
            if (payload.action === 'OFFER_DRAW') {
                if (myColor && payload.actionPlayer !== myColor) {
                    window.showModal("🤝", "Yêu cầu Cầu hòa", "Đối thủ muốn cầu hòa. Bạn có đồng ý không?", "confirm", () => {
                        window.sendAction("ACCEPT_DRAW");
                    });
                }
                return;
            }
            if (payload.action === 'RESIGN' || payload.action === 'TIMEOUT' || payload.action === 'ACCEPT_DRAW') {
                if (statusSpan) {
                    statusSpan.innerHTML = `<span style="color: #ff4d4d; font-weight: bold;">${payload.message}</span>`;
                }
                let icon = payload.action === 'TIMEOUT' ? "⏰" : (payload.action === 'RESIGN' ? "🏳️" : "🤝");
                window.showModal(icon, "Kết thúc trận đấu", payload.message, "alert", () => window.location.href = "/");
                window.scheduleReturnToLobby(5);
                if (window.timerInterval) clearInterval(window.timerInterval);
                // Khóa bàn cờ
                clearHighlights();
                selectedSquare = null;
                return; // Ngừng luồng xử lý nước đi
            }
        }

        if (payload.currentTurn) {
            window.resetTimer(payload.currentTurn);
        }
        
        if (payload.checkmate) {
            if (statusSpan) {
                statusSpan.innerHTML = `<span style="color: #ff4d4d; font-weight: bold;">CHIẾU HẾT! Cờ ${payload.winner === 'WHITE' ? 'Trắng' : 'Đen'} thắng.</span>`;
            }
            if (window.timerInterval) clearInterval(window.timerInterval);
            window.showModal("🏆", "Chiếu hết!", `Đội ${payload.winner === 'WHITE' ? 'Trắng' : 'Đen'} giành chiến thắng!`, "alert", () => window.location.href = "/");
            window.scheduleReturnToLobby(5);
        } else if (payload.check) {
            if (statusSpan) {
                statusSpan.innerHTML = `<span style="color: #ffaa00; font-weight: bold; animation: pulse-text 1s infinite alternate;">ĐANG BỊ CHIẾU TƯỚNG!</span>`;
            }
            
            // Highlight king
            if (payload.kingRow !== undefined && payload.kingCol !== undefined) {
                const checkedKingSquare = squareByPos.get(`${payload.kingRow},${payload.kingCol}`);
                if (checkedKingSquare) {
                    checkedKingSquare.classList.add("checked-king");
                }
            }
        } else {
            // normal state
            if (statusSpan && !payload.action) {
                statusSpan.textContent = "Trận đấu đang diễn ra...";
            }
        }
    };

    // -- TIMER LOGIC --
    window.timerSeconds = 30;
    window.timerInterval = null;
    window.currentTurn = 'WHITE'; // Default

    window.resetTimer = function(newTurn) {
        if (window.timerInterval) clearInterval(window.timerInterval);
        window.currentTurn = newTurn || window.currentTurn;
        window.timerSeconds = 30;
        updateTimerUI();

        const matchStatus = document.querySelector(".match-status span");
        if (matchStatus && (matchStatus.innerText.includes('đang chờ') || 
                            matchStatus.innerText.includes('Cờ') || 
                            matchStatus.innerText.includes('Đầu hàng') || 
                            matchStatus.innerText.includes('Hòa') ||
                            matchStatus.innerText.includes('hết thời gian'))) {
            return;
        }

        window.timerInterval = setInterval(() => {
            window.timerSeconds--;
            updateTimerUI();

            if (window.timerSeconds <= 0) {
                clearInterval(window.timerInterval);
                if (window.currentTurn === myColor) {
                    window.sendAction("TIMEOUT");
                }
            }
        }, 1000);
    };

    function updateTimerUI() {
        const oppTimer = document.getElementById('timer-opponent');
        const youTimer = document.getElementById('timer-you');
        if (!oppTimer || !youTimer) return;

        oppTimer.innerText = '30s';
        youTimer.innerText = '30s';
        oppTimer.style.color = '';
        youTimer.style.color = '';

        if (window.currentTurn === myColor || (myColor === null && window.currentTurn === 'BLACK')) {
            // Nếu là khán giả, coi như "Bạn" là quân Đen, "Đối thủ" là quân Trắng (tùy view logic, nhưng thôi fallback dễ).
            const activeTimer = (window.currentTurn === myColor) ? youTimer : (myColor == null && window.currentTurn === 'BLACK' ? youTimer : oppTimer);
            if(window.currentTurn === myColor) {
                youTimer.innerText = window.timerSeconds + 's';
                if (window.timerSeconds <= 5) youTimer.style.color = '#ff4d4d';
            } else if (myColor == null) {
                 if(window.currentTurn === 'WHITE') {
                    oppTimer.innerText = window.timerSeconds + 's';
                    if (window.timerSeconds <= 5) oppTimer.style.color = '#ff4d4d';
                 } else {
                    youTimer.innerText = window.timerSeconds + 's';
                    if (window.timerSeconds <= 5) youTimer.style.color = '#ff4d4d';
                 }
            }
        } else {
            oppTimer.innerText = window.timerSeconds + 's';
            if (window.timerSeconds <= 5) oppTimer.style.color = '#ff4d4d';
        }
    }

    // Gửi Action
    window.sendAction = function(actionType) {
        if (!myColor) return;
        fetch(`/api/game/${gameId}/action`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ action: actionType })
        }).catch(e => console.error(e));
    };

    // Button Events
    const btnResign = document.getElementById('btn-resign');
    if (btnResign) {
        btnResign.addEventListener('click', () => {
            window.showModal("🏳️", "Đầu hàng", "Bạn có chắc chắn muốn đầu hàng không?", "confirm", () => {
                window.sendAction("RESIGN");
            });
        });
    }

    const btnDraw = document.getElementById('btn-draw');
    if (btnDraw) {
        btnDraw.addEventListener('click', () => {
            window.sendAction("OFFER_DRAW");
            window.showModal("📩", "Đã Gửi", "Đã gửi lời mời cầu hòa tới đối thủ.", "alert");
        });
    }
    
    // Khởi động đồng hồ đếm ban đầu nếu đã tải xong bàn cờ
    window.resetTimer('WHITE');
});