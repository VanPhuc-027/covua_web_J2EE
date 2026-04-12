package com.group18.chessgame.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLEncoder;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class StockfishApiService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, String> moveCache = new ConcurrentHashMap<>();

    public String getBestMoveFromApi(String fen, int depth) {
        String cacheKey = fen.trim() + "_d" + depth;
        if (moveCache.containsKey(cacheKey)) {
            return moveCache.get(cacheKey);
        }

        try {
            String encodedFen = URLEncoder.encode(fen.trim(), "UTF-8").replace("+", "%20");
            String url = "https://stockfish.online/api/s/v2.php?fen=" + encodedFen + "&depth=" + depth;

            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            
            if (root.path("success").asBoolean()) {
                String bestMoveLine = root.path("bestmove").asText();
                if (bestMoveLine != null && bestMoveLine.contains(" ")) {
                    String bestMove = bestMoveLine.split(" ")[1];
                    moveCache.put(cacheKey, bestMove);
                    return bestMove;
                }
            }
        } catch (HttpClientErrorException.TooManyRequests e) {
            System.err.println("Stockfish API Rate Limit: 429 Too Many Requests");
        } catch (Exception e) {
            System.err.println("Stockfish API Error: " + e.getMessage());
        }
        return null;
    }
}