document.addEventListener("DOMContentLoaded", function () {

    let selectedSquare = null;
    let pendingMove = null;

    function clearHighlights() {
        document.querySelectorAll(".square").forEach(s => {
            s.classList.remove("selected");
            s.classList.remove("valid-move");
            s.classList.remove("valid-capture");
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
            s.classList.remove("valid-move");
            s.classList.remove("valid-capture");
        });

        try {
            const res = await fetch(`/api/game/valid-moves?row=${encodeURIComponent(fromRow)}&col=${encodeURIComponent(fromCol)}`);
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

    async function movePiece(fromRow, fromCol, toRow, toCol, promotion) {
        try {
            const res = await fetch("/api/game/move", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    fromRow: parseInt(fromRow),
                    fromCol: parseInt(fromCol),
                    toRow: parseInt(toRow),
                    toCol: parseInt(toCol),
                    promotion: promotion
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

    async function handleMove(fromRow, fromCol, toRow, toCol){
        const pieceImg = document.querySelector(
            `.square[data-row='${fromRow}'][data-col='${fromCol}'] img`
        );
        if(!pieceImg){
            return;
        }
        // kiểm tra pawn
        const src = pieceImg.getAttribute("src");
        if(src.includes("pawn") || src.includes("p")) {
            if(toRow == 0 || toRow == 7){
                const color = pieceImg.src.includes("white") ? "white" : "black";
                pendingMove = {fromRow, fromCol, toRow, toCol};
                showPromotionModal(color);
                return;
            }
        }
        await movePiece(fromRow, fromCol, toRow, toCol, null);
    }

    function showPromotionModal(color){
        const modal = document.getElementById("promotionModal");
        document.querySelectorAll(".promotion-piece").forEach(img => {

            const piece = img.dataset.piece.toLowerCase();

            img.src = `/img/${color}_${piece}.png`;
        });
        modal.classList.remove("hidden");
    }

    document.querySelectorAll(".promotion-piece").forEach(piece => {
        piece.addEventListener("click", async () => {

            const promotion = piece.dataset.piece;

            document.getElementById("promotionModal").classList.add("hidden");

            await movePiece(
                pendingMove.fromRow,
                pendingMove.fromCol,
                pendingMove.toRow,
                pendingMove.toCol,
                promotion
            );

            pendingMove = null;

        });
    });

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

                // click lại ô đang chọn -> bỏ chọn
                if (selectedSquare.row === row && selectedSquare.col === col) {
                    clearHighlights();
                    selectedSquare = null;
                    return;
                }

                // nếu click vào 1 quân khác thì chuyển selection
                if (!square.classList.contains("valid-move") && !square.classList.contains("valid-capture")) {
                    if (isPieceSquare(square)) {
                        clearHighlights();
                        selectedSquare = {row, col};
                        square.classList.add("selected");
                        await highlightValidMoves(row, col);
                    }
                    return;
                }

                await handleMove(selectedSquare.row, selectedSquare.col, row, col);

                clearHighlights();

                selectedSquare = null;
            }

        });

    });

});