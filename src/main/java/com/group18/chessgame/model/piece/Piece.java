package com.group18.chessgame.model.piece;

import com.group18.chessgame.enums.PieceColor;
import com.group18.chessgame.model.Board;
import com.group18.chessgame.model.Spot;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Piece {
    private PieceColor color;
    private String name;
    private String iconPath;
    public Piece(PieceColor color, String name) {
        this.color = color;
        this.name = name;
        this.iconPath = "/img/" + color.toString().toLowerCase() + "_" + name.toLowerCase() + ".png";
    }
    public abstract boolean canMove(Board board, Spot start, Spot end);
}
