document.addEventListener("DOMContentLoaded", function () {
    const gameId = window.CURRENT_GAME_ID;
    if (!gameId || gameId === 'default_id') {
        console.error("Không lấy được ID phòng!");
        return;
    }
    const socket = new SockJS('/ws');
    window.stompClient = Stomp.over(socket);
    window.stompClient.debug = null;

    window.stompClient.connect({}, function (frame) {
        window.stompClient.subscribe('/topic/game/' + gameId, function (message) {
            if (message.body === "PLAYER_JOINED") {
                window.location.reload();
            } else if (message.body === "BOARD_UPDATED") {
                if (typeof window.fetchAndRenderBoard === 'function') {
                    window.fetchAndRenderBoard();
                }
            }
        });
        window.stompClient.subscribe('/topic/game/' + window.CURRENT_GAME_ID + '/chat', function (message) {
            const chatMessage = JSON.parse(message.body);
            if (typeof window.renderChatMessage === 'function') {
                window.renderChatMessage(chatMessage);
            }
        });
    }, function (error) {
        console.error('WebSocket Error: ', error);
    });
});