package com.clewi.javachess.model;

import com.clewi.javachess.pieces.Piece;
import java.awt.Point;

public class Move {
    private final Point source;
    private final Point destination;
    private final Piece piece;
    private final MoveType moveType;

    public Move(Point source, Point destination, Piece piece, MoveType moveType) {
        this.source = source;
        this.destination = destination;
        this.piece = piece;
        this.moveType = moveType;
    }

    public Point getSource() {
        return source;
    }

    public Point getDestination() {
        return destination;
    }

    public Piece getPiece() {
        return piece;
    }

    public MoveType getMoveType() {
        return moveType;
    }
}
