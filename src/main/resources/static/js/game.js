document.addEventListener("DOMContentLoaded", function () {
    const gameId = window.CURRENT_GAME_ID; // Lấy ID phòng từ HTML
    let selectedSquare = null;

    function clearHighlights() {
        document.querySelectorAll(".square").forEach(s => {
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
                const square = document.querySelector(`.square[data-row="${r}"][data-col="${c}"]`);
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

    async function highlightValidMoves(fromRow, fromCol) {
        document.querySelectorAll(".square").forEach(s => {
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

                const target = document.querySelector(`.square[data-row="${r}"][data-col="${c}"]`);
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

    document.querySelectorAll(".square").forEach(square => {
        square.addEventListener("click", async () => {
            const row = square.dataset.row;
            const col = square.dataset.col;

            console.log("Clicked:", row, col);

            if (!selectedSquare) {
                if (!isPieceSquare(square)) return;
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
});