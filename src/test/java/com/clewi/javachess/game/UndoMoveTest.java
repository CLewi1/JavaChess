package com.clewi.javachess.game;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.clewi.javachess.model.*;
import com.clewi.javachess.pieces.*;
import java.awt.Point;

public class UndoMoveTest {

    @Test
    public void testPawnUndoPreservesInitialState() {
        // Create a board with pieces in starting positions
        Board board = new Board();
        GameManager gameManager = new GameManager();
        
        // Get the pawn at e2 (white pawn)
        Piece pawn = board.getPiece(4, 6);
        assertNotNull(pawn, "Pawn should exist at e2");
        assertTrue(pawn instanceof Pawn, "Piece should be a pawn");
        assertFalse(pawn.getHasMoved(), "Pawn should not have moved initially");
        
        // Create a move for the pawn from e2 to e4 (two squares)
        Move move = new Move(new Point(4, 6), new Point(4, 4), pawn, MoveType.NORMAL);
        
        // Make the move
        gameManager.makeMove(move);
        
        // Verify the pawn has moved
        assertTrue(pawn.getHasMoved(), "Pawn should have hasMoved=true after moving");
        assertEquals(new Point(4, 4), pawn.getPosition(), "Pawn should be at new position");
        
        // Undo the move
        boolean undoSuccess = gameManager.undoMove();
        assertTrue(undoSuccess, "Undo should succeed");
        
        // Verify the pawn is back to its original state
        assertEquals(new Point(4, 6), pawn.getPosition(), "Pawn should be back at original position");
        assertFalse(pawn.getHasMoved(), "Pawn should have hasMoved=False after undo (original state)");
        
        // Try to make a two-square move again to verify it's allowed
        // This should be valid since the pawn's hasMoved is back to false
        // (We can't easily test move validation here without more setup, but we verified the state is correct)
    }
    
    @Test
    public void testPawnUndoAfterSecondMove() {
        // Create a board and game manager
        Board board = new Board();
        GameManager gameManager = new GameManager();
        
        // Get the pawn at e2 (white pawn)
        Piece pawn = board.getPiece(4, 6);
        
        // First move: e2 to e3 (one square)
        Move move1 = new Move(new Point(4, 6), new Point(4, 5), pawn, MoveType.NORMAL);
        gameManager.makeMove(move1);
        
        // Switch to black player and make a dummy move, then back to white
        // (This is needed because the game alternates turns)
        
        // Second move: e3 to e4 (one square)
        Move move2 = new Move(new Point(4, 5), new Point(4, 4), pawn, MoveType.NORMAL);
        
        // Before second move, pawn should have hasMoved=true
        assertTrue(pawn.getHasMoved(), "Pawn should have hasMoved=true before second move");
        
        // Make second move (this will update hasMoved state in the move object)
        gameManager.makeMove(move2);
        
        // Undo the second move
        boolean undoSuccess = gameManager.undoMove();
        assertTrue(undoSuccess, "Undo should succeed");
        
        // Verify the pawn is back at intermediate position with hasMoved=true
        assertEquals(new Point(4, 5), pawn.getPosition(), "Pawn should be back at intermediate position");
        assertTrue(pawn.getHasMoved(), "Pawn should still have hasMoved=true after undoing second move");
    }
}
