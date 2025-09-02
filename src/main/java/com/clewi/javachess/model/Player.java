package com.clewi.javachess.model;

import com.clewi.javachess.pieces.Piece;
import java.util.ArrayList;
import java.util.List;

public class Player {
    private final boolean isWhite;
    private List<Piece> capturedPieces;

    public Player(boolean isWhite) {
        this.isWhite = isWhite;
        this.capturedPieces = new ArrayList<>();
    }

    public boolean isWhite() {
        return isWhite;
    }

    public void addCapturedPiece(Piece piece) {
        capturedPieces.add(piece);
    }

    public List<Piece> getCapturedPieces() {
        return capturedPieces;
    }
}
