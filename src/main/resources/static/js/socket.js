document.addEventListener("DOMContentLoaded", function () {
    const gameId = window.CURRENT_GAME_ID;
    if (!gameId || gameId === 'default_id') {
        console.error("Không lấy được ID phòng!");
        return;
    }
    const socket = new SockJS('/ws');
    const stompClient = Stomp.over(socket);
    stompClient.debug = null;
    stompClient.connect({}, function (frame) {
        console.log('Đã kết nối WebSocket thành công. Đang nghe tại phòng: ' + gameId);
        stompClient.subscribe('/topic/game/' + gameId, function (message) {
            if (message.body === "PLAYER_JOINED") {
                console.log("Đối thủ đã kết nối! Đang làm mới bàn cờ...");
                window.location.reload();
            }
        });
    }, function (error) {
        console.error('Lỗi kết nối WebSocket: ', error);
    });
});