document.addEventListener("DOMContentLoaded", function () {

    window.highlightValidMoves = async function (fromRow, fromCol) {
        window.squares.forEach(s => {
            s.classList.remove("valid-move", "valid-capture");
        });

        let moves = window.validMoves ? window.validMoves[`${fromRow},${fromCol}`] : null;

        if (!Array.isArray(moves)) {
            try {
                const res = await fetch(`/api/game/${window.gameId}/valid-moves?row=${fromRow}&col=${fromCol}`);
                if (res.ok) {
                    moves = await res.json();
                }
            } catch (e) {
                console.error("Failed to fetch valid moves from API:", e);
                return;
            }
        }

        if (!Array.isArray(moves)) return;

        if (window.selectedSquare == null || window.selectedSquare.row != fromRow || window.selectedSquare.col != fromCol) {
            return;
        }

        window.squares.forEach(s => {
            s.classList.remove("valid-move", "valid-capture");
        });

        moves.forEach(move => {
            const r = move?.[0];
            const c = move?.[1];
            if (typeof r !== "number" || typeof c !== "number") return;

            const target = window.squareByPos.get(`${r},${c}`);
            if (!target) return;

            if (window.isPieceSquare(target)) {
                target.classList.add("valid-capture");
            } else {
                target.classList.add("valid-move");
            }
        });
    };

    window.isValidMoveTarget = async function (fromRow, fromCol, toRow, toCol) {
        let moves = window.validMoves ? window.validMoves[`${fromRow},${fromCol}`] : null;

        if (!Array.isArray(moves)) {
            try {
                const res = await fetch(`/api/game/${window.gameId}/valid-moves?row=${fromRow}&col=${fromCol}`);
                if (res.ok) {
                    moves = await res.json();
                }
            } catch (e) {
                console.error("Failed to fetch valid moves from API:", e);
                return false;
            }
        }

        if (!Array.isArray(moves)) return false;

        return moves.some(move =>
            String(move[0]) === String(toRow) && String(move[1]) === String(toCol)
        );
    };


    window.movePiece = async function (fromRow, fromCol, toRow, toCol, promotion = null) {
        if (!window.myColor) return false;

        const square = window.squareByPos.get(`${fromRow},${fromCol}`);
        const img = square?.querySelector("img");
        const isPawn = img && img.getAttribute("src").toLowerCase().includes("pawn");
        const targetSquare = window.squareByPos.get(`${toRow},${toCol}`);
        let isCapture = targetSquare && targetSquare.querySelector("img") !== null;
        
        if (isPawn && fromCol !== toCol && !isCapture) {
            isCapture = true;
        }

        const isLastRank = (window.myColor === "WHITE" && parseInt(toRow) === 0) || (window.myColor === "BLACK" && parseInt(toRow) === 7);
        if (isPawn && isLastRank && !promotion) {
            window.showPromotionModal(fromRow, fromCol, toRow, toCol);
            return false;
        }

        const capturedImg = targetSquare?.querySelector("img");
        if (capturedImg) capturedImg.remove();
        if (img && targetSquare) targetSquare.appendChild(img);

        try {
            const res = await fetch(`/api/game/${window.gameId}/move`, {
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
                window.renderBoardFromResponse(data.board);
                window.playMoveSound(false, promotion !== null, isCapture, false);

                if (typeof window.handleCheckStatus === "function") {
                    window.handleCheckStatus(data);
                }

                if (window.isBotMode && data.currentTurn === "BLACK") {
                    setTimeout(() => {
                        fetch(`/api/game/${window.gameId}/bot-move?depth=${window.botDepth}`, { method: "POST" })
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
    };


    window.squares.forEach(square => {
        square.addEventListener("click", async () => {
            if (window.myColor && window.currentTurn !== window.myColor) return;

            const row = square.dataset.row;
            const col = square.dataset.col;

            if (!window.selectedSquare) {
                if (!window.isPieceSquare(square)) return;

                if (window.myColor) {
                    const img = square.querySelector("img");
                    const pieceColor = img && img.getAttribute("src").includes("white_") ? "WHITE" : "BLACK";
                    if (pieceColor !== window.myColor) return;
                }

                window.clearHighlights();
                window.selectedSquare = { row, col };
                square.classList.add("selected");

                if (window.isHintsEnabled()) {
                    await window.highlightValidMoves(row, col);
                }
                return;
            }

            if (window.selectedSquare.row === row && window.selectedSquare.col === col) {
                window.clearHighlights();
                window.selectedSquare = null;
                return;
            }

            const clickedOwnPiece = window.isPieceSquare(square) && (() => {
                if (!window.myColor) return true;
                const img = square.querySelector("img");
                const pieceColor = img && img.getAttribute("src").includes("white_") ? "WHITE" : "BLACK";
                return pieceColor === window.myColor;
            })();

            const validByClass =
                square.classList.contains("valid-move") ||
                square.classList.contains("valid-capture");

            const validByData = await window.isValidMoveTarget(
                window.selectedSquare.row,
                window.selectedSquare.col,
                row,
                col
            );

            if (!validByClass && !validByData) {
                if (clickedOwnPiece) {
                    window.clearHighlights();
                    window.selectedSquare = { row, col };
                    square.classList.add("selected");

                    if (window.isHintsEnabled()) {
                        await window.highlightValidMoves(row, col);
                    }
                }
                return;
            }

            await window.movePiece(window.selectedSquare.row, window.selectedSquare.col, row, col);

            const promotionOverlay = document.getElementById("promotion-modal-overlay");
            if (!promotionOverlay || promotionOverlay.style.display !== "flex") {
                window.clearHighlights();
                window.selectedSquare = null;
            }
        });
    });
});
