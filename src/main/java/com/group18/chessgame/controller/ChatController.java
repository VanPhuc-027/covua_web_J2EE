package com.group18.chessgame.controller;

import com.group18.chessgame.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, List<ChatMessage>> chatHistoryMap = new ConcurrentHashMap<>();

    @MessageMapping("/chat/{gameId}")
    public void sendMessage(@DestinationVariable String gameId, @Payload ChatMessage message) {
        chatHistoryMap.computeIfAbsent(gameId, k -> new CopyOnWriteArrayList<>()).add(message);
        String destination = "/topic/game/" + gameId + "/chat";
        messagingTemplate.convertAndSend(destination, message);
    }

    @GetMapping("/api/game/{gameId}/chat-history")
    @ResponseBody
    public ResponseEntity<List<ChatMessage>> getChatHistory(@PathVariable String gameId) {
        List<ChatMessage> history = chatHistoryMap.getOrDefault(gameId, new CopyOnWriteArrayList<>());
        return ResponseEntity.ok(history);
    }
}