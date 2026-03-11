package com.group18.chessgame.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private long id;
    private String username;
    private String email;
    private String password;
    private int eloRating = 1200;
    private LocalDateTime createdAt;
}
