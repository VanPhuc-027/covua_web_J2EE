document.addEventListener("DOMContentLoaded", function () {

    window.gameId = window.CURRENT_GAME_ID;
    window.isWhite = window.CURRENT_USERNAME === window.WHITE_PLAYER;
    window.isBlack = window.CURRENT_USERNAME === window.BLACK_PLAYER;


    window.isBotMode = sessionStorage.getItem("isBotMode") === "true";
    window.botDepth = sessionStorage.getItem("botDepth") || 10;


    window.myColor = window.isWhite ? 'WHITE' : (window.isBlack ? 'BLACK' : (window.isBotMode ? 'WHITE' : null));


    window.selectedSquare = null;
    window.squares = Array.from(document.querySelectorAll(".square"));
    window.squareByPos = new Map();
    
    window.squares.forEach(sq => {
        const r = sq.dataset.row;
        const c = sq.dataset.col;
        if (r != null && c != null) {
            window.squareByPos.set(`${r},${c}`, sq);
        }
    });


    window.isSoundEnabled = function() {
        return localStorage.getItem('chess_sound') !== 'false';
    };

    window.isHintsEnabled = function() {
        return localStorage.getItem('chess_hints') !== 'false';
    };

    window.soundMove = new Audio('/sounds/move-self.mp3');
    window.soundCapture = new Audio('/sounds/capture.mp3');
    window.soundCheck = new Audio('/sounds/move-check.mp3');
    window.soundCastle = new Audio('/sounds/castle.mp3');
    window.soundPromote = new Audio('/sounds/promote.mp3');
    window.soundNotify = new Audio('/sounds/notify.mp3');

    window.playMoveSound = function(isCheck, isPromote, isCapture, isCastle) {
        if (!window.isSoundEnabled()) return;

        try {
            if (isCheck) {
                window.soundCheck.currentTime = 0;
                window.soundCheck.play().catch(e => console.log(e));
            } else if (isPromote) {
                window.soundPromote.currentTime = 0;
                window.soundPromote.play().catch(e => console.log(e));
            } else if (isCapture) {
                window.soundCapture.currentTime = 0;
                window.soundCapture.play().catch(e => console.log(e));
            } else if (isCastle) {
                window.soundCastle.currentTime = 0;
                window.soundCastle.play().catch(e => console.log(e));
            } else {
                window.soundMove.currentTime = 0;
                window.soundMove.play().catch(e => console.log(e));
            }
        } catch (err) {
            console.error("Lỗi phát âm thanh:", err);
        }
    };


    if (window.isBlack) {
        const boardEl = document.querySelector('.chessboard');
        if (boardEl) boardEl.classList.add('flipped');
    }
});
