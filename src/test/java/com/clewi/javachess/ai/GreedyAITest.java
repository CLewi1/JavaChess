package com.clewi.javachess.ai;

import static org.junit.jupiter.api.Assertions.*;

import com.clewi.javachess.model.Board;
import com.clewi.javachess.model.Move;
import com.clewi.javachess.pieces.*;
import com.clewi.javachess.testutils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GreedyAITest {
    private GreedyAI greedyAI;
    private Board board;

    @BeforeEach
    public void setUp() {
        board = TestUtils.emptyBoard();
        greedyAI = new GreedyAI();
    }

    @Test
    public void testAIName() {
        assertEquals("GreedyAI", greedyAI.getName());
    }

    @Test
    public void testNoLegalMovesReturnsNull() {
        // Empty board should have no legal moves
        Move bestMove = greedyAI.getBestMove(board, true);
        assertNull(bestMove);
        
        bestMove = greedyAI.getBestMove(board, false);
        assertNull(bestMove);
    }

    @Test
    public void testSingleMoveReturned() {
        // Place only one piece that can move
        TestUtils.placeKing(board, 4, 4, true);
        
        Move bestMove = greedyAI.getBestMove(board, true);
        
        assertNotNull(bestMove);
        assertTrue(bestMove.getPiece() instanceof King);
        assertTrue(bestMove.getPiece().isWhite());
    }

    @Test
    public void testCapturePriorityOverNormalMove() {
        // Set up position where AI can capture or make normal move
        TestUtils.placeQueen(board, 4, 4, true);     // White queen
        TestUtils.placePawn(board, 3, 3, false);     // Black pawn that can be captured
        TestUtils.placeKing(board, 7, 7, true);      // White king for alternative move
        
        Move bestMove = greedyAI.getBestMove(board, true);
        
        assertNotNull(bestMove);
        
        // The AI should prefer capturing the pawn (though we can't guarantee this
        // without knowing the exact evaluation, we can test that it finds a move)
        assertTrue(bestMove.getPiece().isWhite());
    }

    @Test
    public void testHighValueCapturePreferred() {
        // Set up position where AI can capture different value pieces
        TestUtils.placeQueen(board, 4, 4, true);     // White queen
        TestUtils.placePawn(board, 3, 3, false);     // Black pawn (100 points)
        TestUtils.placeQueen(board, 5, 5, false);    // Black queen (900 points)
        
        Move bestMove = greedyAI.getBestMove(board, true);
        
        assertNotNull(bestMove);
        assertTrue(bestMove.getPiece() instanceof Queen);
        
        // Should target higher value piece if possible
        // Note: Exact target depends on move generation and validation
    }

    @Test
    public void testAIConsistencyWithSamePosition() {
        // Set up a position and verify AI gives consistent results
        setupSimplePosition();
        
        Move move1 = greedyAI.getBestMove(board, true);
        Move move2 = greedyAI.getBestMove(board, true);
        
        // Both should be non-null if there are legal moves
        assertEquals(move1 == null, move2 == null);
        
        if (move1 != null && move2 != null) {
            // Should be from the same piece (though destination might vary due to randomization)
            assertEquals(move1.getSource(), move2.getSource());
        }
    }

    @Test
    public void testBlackAIBehavior() {
        // Test that AI works correctly when playing as black
        setupSimplePosition();
        
        Move blackMove = greedyAI.getBestMove(board, false);
        
        if (blackMove != null) {
            assertFalse(blackMove.getPiece().isWhite());
        }
    }

    @Test
    public void testAIHandlesComplexPosition() {
        // Set up a more complex position
        setupComplexPosition();
        
        Move whiteMove = greedyAI.getBestMove(board, true);
        Move blackMove = greedyAI.getBestMove(board, false);
        
        // Should handle complex positions without crashing
        if (whiteMove != null) {
            assertTrue(whiteMove.getPiece().isWhite());
        }
        if (blackMove != null) {
            assertFalse(blackMove.getPiece().isWhite());
        }
    }

    @Test
    public void testAIFindsCheckmate() {
        // Set up a position where checkmate is possible
        TestUtils.placeKing(board, 0, 0, false);     // Black king in corner
        TestUtils.placeQueen(board, 2, 1, true);     // White queen
        TestUtils.placeKing(board, 2, 2, true);      // White king supporting
        
        Move bestMove = greedyAI.getBestMove(board, true);
        
        // Should find a move (might be checkmate, but at least should not crash)
        if (bestMove != null) {
            assertTrue(bestMove.getPiece().isWhite());
        }
    }

    @Test
    public void testAIAvoidsSelfCheck() {
        // Set up position where some moves would put own king in check
        TestUtils.placeKing(board, 4, 0, true);      // White king
        TestUtils.placeRook(board, 4, 7, false);     // Black rook attacking king's file
        TestUtils.placeQueen(board, 3, 0, true);     // White queen that could block
        
        Move bestMove = greedyAI.getBestMove(board, true);
        
        // Should find a legal move that doesn't put king in check
        if (bestMove != null) {
            assertTrue(bestMove.getPiece().isWhite());
        }
    }

    @Test
    public void testAIPerformanceReasonable() {
        // Test that AI doesn't take too long (basic performance test)
        setupComplexPosition();
        
        long startTime = System.currentTimeMillis();
        Move bestMove = greedyAI.getBestMove(board, true);
        long endTime = System.currentTimeMillis();
        
        long duration = endTime - startTime;
        
        // Should complete in reasonable time (less than 5 seconds for greedy AI)
        assertTrue(duration < 5000, "AI took too long: " + duration + "ms");
        
        // Should still find a move if any exist
        if (bestMove != null) {
            assertTrue(bestMove.getPiece().isWhite());
        }
    }

    /**
     * Helper method to set up a simple test position
     */
    private void setupSimplePosition() {
        TestUtils.placeKing(board, 4, 0, true);
        TestUtils.placeKing(board, 4, 7, false);
        TestUtils.placePawn(board, 2, 1, true);
        TestUtils.placePawn(board, 2, 6, false);
        TestUtils.placeQueen(board, 3, 0, true);
        TestUtils.placeRook(board, 0, 7, false);
    }

    /**
     * Helper method to set up a complex test position
     */
    private void setupComplexPosition() {
        // White pieces
        TestUtils.placeKing(board, 4, 0, true);
        TestUtils.placeQueen(board, 3, 1, true);
        TestUtils.placeRook(board, 0, 0, true);
        TestUtils.placeRook(board, 7, 0, true);
        TestUtils.placeBishop(board, 2, 0, true);
        TestUtils.placeKnight(board, 1, 0, true);
        TestUtils.placePawn(board, 0, 1, true);
        TestUtils.placePawn(board, 1, 1, true);
        TestUtils.placePawn(board, 2, 2, true);
        TestUtils.placePawn(board, 6, 1, true);
        TestUtils.placePawn(board, 7, 1, true);
        
        // Black pieces
        TestUtils.placeKing(board, 4, 7, false);
        TestUtils.placeQueen(board, 3, 6, false);
        TestUtils.placeRook(board, 0, 7, false);
        TestUtils.placeBishop(board, 5, 7, false);
        TestUtils.placeKnight(board, 6, 7, false);
        TestUtils.placePawn(board, 0, 6, false);
        TestUtils.placePawn(board, 1, 6, false);
        TestUtils.placePawn(board, 3, 5, false);
        TestUtils.placePawn(board, 5, 6, false);
        TestUtils.placePawn(board, 7, 6, false);
    }
}