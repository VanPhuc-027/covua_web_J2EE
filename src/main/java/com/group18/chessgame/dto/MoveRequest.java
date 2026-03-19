package com.group18.chessgame.dto;

import lombok.Data;

@Data
public class MoveRequest {

    private int fromRow;
    private int fromCol;
    private int toRow;
    private int toCol;
    private String promotion; // queen, rook, bishop, knight
}