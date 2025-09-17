package com.clewi.javachess.testutils;

import com.clewi.javachess.model.Board;
import com.clewi.javachess.pieces.Pawn;
import com.clewi.javachess.pieces.King;
import com.clewi.javachess.pieces.Knight;
import com.clewi.javachess.pieces.Queen;
import com.clewi.javachess.pieces.Rook;
import com.clewi.javachess.pieces.Bishop;

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

    public static Rook placeRook(Board board, int x, int y, boolean isWhite) {
        Rook r = new Rook(x, y, isWhite, "Rook.png", board, false);
        r.setBoard(board);
        board.setSquare(x, y, r);
        board.trackPiece(r);
        return r;
    }

    public static Bishop placeBishop(Board board, int x, int y, boolean isWhite) {
        Bishop b = new Bishop(x, y, isWhite, "Bishop.png", board, false);
        b.setBoard(board);
        board.setSquare(x, y, b);
        board.trackPiece(b);
        return b;
    }
    
    /**
     * Sets up a typical middlegame position with multiple pieces for both sides
     */
    public static void setupMiddlegamePosition(Board board) {
        board.clear();
        
        // White pieces - scattered middlegame setup
        placeKing(board, 6, 7, true);        // White king on g1
        placeQueen(board, 3, 4, true);       // White queen on d4
        placePawn(board, 4, 5, true);        // White pawn on e3
        placePawn(board, 5, 6, true);        // White pawn on f2
        placeKnight(board, 5, 5, true);      // White knight on f3
        placeBishop(board, 2, 4, true);      // White bishop on c4
        placeRook(board, 0, 7, true);        // White rook on a1
        
        // Black pieces - defending middlegame setup
        placeKing(board, 4, 0, false);       // Black king on e8
        placeQueen(board, 3, 1, false);      // Black queen on d7
        placePawn(board, 4, 2, false);       // Black pawn on e6
        placePawn(board, 3, 2, false);       // Black pawn on d6
        placeKnight(board, 6, 0, false);     // Black knight on g8
        placeBishop(board, 5, 0, false);     // Black bishop on f8
        placeRook(board, 7, 0, false);       // Black rook on h8
    }
    
    /**
     * Sets up a typical endgame position with fewer pieces
     */
    public static void setupEndgamePosition(Board board) {
        board.clear();
        
        // White pieces - endgame with king, queen, and pawn
        placeKing(board, 6, 6, true);        // White king on g2
        placeQueen(board, 3, 3, true);       // White queen on d5
        placePawn(board, 4, 4, true);        // White pawn on e4
        
        // Black pieces - defending endgame with king and rook
        placeKing(board, 1, 1, false);       // Black king on b7
        placeRook(board, 7, 1, false);       // Black rook on h7
        placePawn(board, 2, 2, false);       // Black pawn on c6
    }
}
