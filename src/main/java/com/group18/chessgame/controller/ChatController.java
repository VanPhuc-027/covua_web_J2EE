package com.group18.chessgame.controller;

import com.group18.chessgame.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/{gameId}")
    public void sendMessage(@DestinationVariable String gameId, @Payload ChatMessage message) {
        String destination = "/topic/game/" + gameId + "/chat";
        messagingTemplate.convertAndSend(destination, message);
    }
}
