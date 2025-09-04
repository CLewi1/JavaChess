package com.clewi.javachess.testutils;

import com.clewi.javachess.model.Board;
import com.clewi.javachess.pieces.Pawn;
import com.clewi.javachess.pieces.King;
import com.clewi.javachess.pieces.Knight;
import com.clewi.javachess.pieces.Queen;
import java.awt.Point;

/**
 * Lightweight helpers for constructing test board positions.
 */
public final class TestUtils {
    private TestUtils() {}

    public static Board emptyBoard() {
        Board board = new Board(false);
        board.clear();
        return board;
    }

    public static Pawn placePawn(Board board, int x, int y, boolean isWhite) {
        Pawn p = new Pawn(x, y, isWhite, "Pawn.png", board, false);
        p.setBoard(board);
        board.setSquare(x, y, p);
        board.trackPiece(p);
        return p;
    }

    public static King placeKing(Board board, int x, int y, boolean isWhite) {
        King k = new King(x, y, isWhite, "King.png", board, false);
        k.setBoard(board);
        board.setSquare(x, y, k);
        board.trackPiece(k);
        return k;
    }

    public static Knight placeKnight(Board board, int x, int y, boolean isWhite) {
        Knight n = new Knight(x, y, isWhite, "Knight.png", board, false);
        n.setBoard(board);
        board.setSquare(x, y, n);
        board.trackPiece(n);
        return n;
    }

    public static Queen placeQueen(Board board, int x, int y, boolean isWhite) {
        Queen q = new Queen(x, y, isWhite, "Queen.png", board, false);
        q.setBoard(board);
        board.setSquare(x, y, q);
        board.trackPiece(q);
        return q;
    }
}
