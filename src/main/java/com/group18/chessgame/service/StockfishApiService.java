package com.group18.chessgame.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLEncoder;

@Service
public class StockfishApiService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getBestMoveFromApi(String fen, int depth) {
        try {
            String clearFen = fen.trim();
            String boardPart = clearFen.split(" ")[0];
            String perfectFen = boardPart + " b - - 0 1";
            String encodedFen = URLEncoder.encode(perfectFen, "UTF-8").replace("+", "%20");
            String url = "https://stockfish.online/api/s/v2.php?fen=" + encodedFen + "&depth=" + depth;

            String response = restTemplate.getForObject(url, String.class);

            JsonNode root = objectMapper.readTree(response);
            if (root.path("success").asBoolean()) {
                String bestMoveLine = root.path("bestmove").asText();
                return bestMoveLine.split(" ")[1];
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi gọi API Stockfish: " + e.getMessage());
        }
        return null;
    }
}