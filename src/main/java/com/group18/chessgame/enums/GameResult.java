package com.group18.chessgame.enums;

public enum GameResult {
    WHITE_WINS, // White player has won the game
    BLACK_WINS, // Black player has won the game
    STALEMATE, // Game has ended in a stalemate
    CHECKMATE, // Game has ended in a checkmate
    RESIGNATION, // A player has resigned from the game
    TIMEOUT, // A player has run out of time
    FORFEIT // A player has forfeited the game
}
