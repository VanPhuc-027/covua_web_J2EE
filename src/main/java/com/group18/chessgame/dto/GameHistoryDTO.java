package com.group18.chessgame.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class GameHistoryDTO {
    private String gameId;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private String result;
    private String opponentName;
    private int eloChange;
}
