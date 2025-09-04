package com.clewi.javachess.model;

import static org.junit.jupiter.api.Assertions.*;

import com.clewi.javachess.pieces.King;
import com.clewi.javachess.pieces.Pawn;
import org.junit.jupiter.api.Test;
import java.awt.Point;

public class BoardTest {

    @Test
    public void testBoardInitializationHasAllPieces() {
        // Test: after initialization, all pieces should be present and in order
        // White pieces
        Board board = new Board(true);
        assertNotNull(board.getPiece(0, 0), "Rook should be present at (0, 0)");
        assertNotNull(board.getPiece(1, 0), "Knight should be present at (1, 0)");
        assertNotNull(board.getPiece(2, 0), "Bishop should be present at (2, 0)");
        assertNotNull(board.getPiece(3, 0), "Queen should be present at (3, 0)");
        assertNotNull(board.getPiece(4, 0), "King should be present at (4, 0)");
        assertNotNull(board.getPiece(5, 0), "Bishop should be present at (5, 0)");
        assertNotNull(board.getPiece(6, 0), "Knight should be present at (6, 0)");
        assertNotNull(board.getPiece(7, 0), "Rook should be present at (7, 0)");
        for (int i = 0; i < 8; i++) {
            assertNotNull(board.getPiece(i, 1), "Pawn should be present at (" + i + ", 1)");
        }

        // Black Pieces
        assertNotNull(board.getPiece(0, 7), "Rook should be present at (0, 7)");
        assertNotNull(board.getPiece(1, 7), "Knight should be present at (1, 7)");
        assertNotNull(board.getPiece(2, 7), "Bishop should be present at (2, 7)");
        assertNotNull(board.getPiece(3, 7), "Queen should be present at (3, 7)");
        assertNotNull(board.getPiece(4, 7), "King should be present at (4, 7)");
        assertNotNull(board.getPiece(5, 7), "Bishop should be present at (5, 7)");
        assertNotNull(board.getPiece(6, 7), "Knight should be present at (6, 7)");
        assertNotNull(board.getPiece(7, 7), "Rook should be present at (7, 7)");
        for (int i = 0; i < 8; i++) {
            assertNotNull(board.getPiece(i, 6), "Pawn should be present at (" + i + ", 6)");
        }

    }

    @Test
    public void testClearRemovesAllPieces() {
        // Test: clear() removes all pieces and empties tracked lists
        Board board = new Board(true);
        board.clear();
        // After clearing, no piece should be on board and lists should be empty
        assertNull(board.getPiece(0, 0));
        assertTrue(board.getPieces(true).isEmpty());
        assertTrue(board.getPieces(false).isEmpty());
    }

    @Test
    public void testTrackPieceAddsPiece() {
        // Test: trackPiece should add a piece to the board's tracked list
        Board board = new Board(false); // start empty
        Pawn pawn = new Pawn(0, 1, true, "Pawn.png", board, false);
        pawn.setBoard(board);
        board.setSquare(0, 1, pawn);
        board.trackPiece(pawn);
        // The tracked pieces list for white should now contain the pawn
        assertEquals(1, board.getPieces(true).size());
        assertSame(pawn, board.getPiece(new Point(0, 1)));
    }

}
