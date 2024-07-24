package com.clewi.javachess.pieces;

import java.util.List;
import java.util.ArrayList;

public class CapturedPieces<T extends Piece> {
    private List<T> pieces;

    public CapturedPieces() {
        this.pieces = new ArrayList<>();
    }

    public void add(T piece) {
        pieces.add(piece);
    }

    public List<T> getPieces() {
        return pieces;
    }

    public void printCapturedPieces() {
        for (T piece : pieces) {
            System.out.println(piece);
        }
    }
}
