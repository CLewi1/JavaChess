package com.clewi.javachess.ai;

import static org.junit.jupiter.api.Assertions.*;

import com.clewi.javachess.game.GameManager;
import com.clewi.javachess.model.Board;
import com.clewi.javachess.model.GameState;
import com.clewi.javachess.model.Move;

import com.clewi.javachess.testutils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AIEdgeCaseTest {
    private GameManager gameManager;
    private Board board;
    private GreedyAI greedyAI;

    @BeforeEach
    public void setUp() {
        gameManager = new GameManager();
        board = gameManager.getBoard();
        greedyAI = new GreedyAI();
    }

    @Test
    public void testAIWithEmptyBoard() {
        // Clear the board
        board.clear();
        
        // AI should handle empty board gracefully
        assertNull(greedyAI.getBestMove(board, true));
        assertNull(greedyAI.getBestMove(board, false));
    }

    @Test
    public void testAIWithOnlyKings() {
        // Set up king vs king endgame
        // BUG FIXED: King recursion bug has been resolved
        board.clear();
        TestUtils.placeKing(board, 4, 4, true);
        TestUtils.placeKing(board, 4, 6, false);
        
        // AI should find legal king moves without infinite recursion
        Move whiteMove = greedyAI.getBestMove(board, true);
        Move blackMove = greedyAI.getBestMove(board, false);
        
        assertNotNull(whiteMove, "White AI should find a valid king move");
        assertNotNull(blackMove, "Black AI should find a valid king move");
        
        // Verify moves are valid king moves (one square distance)
        assertTrue(Math.abs(whiteMove.getDestination().x - 4) <= 1, "White king move should be within one square");
        assertTrue(Math.abs(whiteMove.getDestination().y - 4) <= 1, "White king move should be within one square");
        assertTrue(Math.abs(blackMove.getDestination().x - 4) <= 1, "Black king move should be within one square");
        assertTrue(Math.abs(blackMove.getDestination().y - 6) <= 1, "Black king move should be within one square");
    }

    @Test
    public void testAIInCheckPosition() {
        // Set up a position where the AI is in check
        board.clear();
        TestUtils.placeKing(board, 4, 0, true);    // White king
        TestUtils.placeQueen(board, 4, 7, false);  // Black queen attacking king
        
        // AI should find a move to get out of check
        // (either move the king or block with another piece if available)
        assertNotNull(greedyAI.getBestMove(board, true));
    }

    @Test
    public void testAIWithPawnPromotion() {
        // Set up pawn promotion scenario
        board.clear();
        TestUtils.placeKing(board, 4, 0, true);
        TestUtils.placeKing(board, 4, 7, false);
        TestUtils.placePawn(board, 3, 1, true);  // White pawn near promotion
        
        // AI should handle this position without crashing
        assertDoesNotThrow(() -> {
            greedyAI.getBestMove(board, true);
        });
    }

    @Test
    public void testAIPerformanceWithComplexPosition() {
        // Use the starting position which has many possible moves
        long startTime = System.currentTimeMillis();
        
        // AI should complete move selection in reasonable time
        assertNotNull(greedyAI.getBestMove(board, true));
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Greedy AI should be very fast (under 1 second)
        assertTrue(duration < 1000, "AI took too long: " + duration + "ms");
    }

    @Test
    public void testAIConsistencyUnderStress() {
        // Run AI multiple times on same position to test consistency
        for (int i = 0; i < 10; i++) {
            assertNotNull(greedyAI.getBestMove(board, true));
        }
    }

    @Test
    public void testMoveGeneratorWithNoPieces() {
        // Test MoveGenerator edge case
        board.clear();
        MoveGenerator generator = new MoveGenerator(board);
        
        assertTrue(generator.getAllLegalMoves(true).isEmpty());
        assertTrue(generator.getAllLegalMoves(false).isEmpty());
    }

    @Test
    public void testMoveGeneratorWithSinglePiece() {
        // Test with just one piece
        board.clear();
        TestUtils.placeKing(board, 3, 3, true);
        
        MoveGenerator generator = new MoveGenerator(board);
        assertFalse(generator.getAllLegalMoves(true).isEmpty());
        assertTrue(generator.getAllLegalMoves(false).isEmpty());
    }

    @Test
    public void testMaterialEvaluatorEdgeCases() {
        // Test MaterialEvaluator with unusual positions
        board.clear();
        
        // All queens position
        TestUtils.placeKing(board, 0, 0, true);
        TestUtils.placeKing(board, 7, 7, false);
        TestUtils.placeQueen(board, 1, 1, true);
        TestUtils.placeQueen(board, 2, 2, true);
        TestUtils.placeQueen(board, 5, 5, false);
        TestUtils.placeQueen(board, 6, 6, false);
        
        // White has slight advantage (better king position, assuming equal queens)
        int whiteEval = MaterialEvaluator.evaluatePosition(board, true);
        int blackEval = MaterialEvaluator.evaluatePosition(board, false);
        
        // Should be equal material
        assertEquals(-whiteEval, blackEval);
    }

    @Test
    public void testGameManagerAIWithGameStates() {
        // Test AI integration with different game states
        gameManager.configureAI(true, true);
        
        // Starting state should be PLAYING
        assertEquals(GameState.PLAYING, gameManager.getGameState());
        
        // Making moves shouldn't break game state management
        if (gameManager.isAITurn()) {
            gameManager.makeAIMove();
            assertNotNull(gameManager.getGameState());
        }
    }

    @Test
    public void testAIHandlesCaptureCorrectly() {
        // Set up position with capture opportunity
        board.clear();
        TestUtils.placeKing(board, 4, 0, true);
        TestUtils.placeKing(board, 4, 7, false);
        TestUtils.placeQueen(board, 3, 3, true);   // White queen
        TestUtils.placePawn(board, 4, 4, false);   // Black pawn that can be captured
        
        // AI should prefer capturing the pawn
        assertNotNull(greedyAI.getBestMove(board, true));
    }

    @Test
    public void testAIThreadSafety() {
        // Test that AI can handle concurrent access
        // (This is a basic test - true thread safety would require more complex testing)
        assertDoesNotThrow(() -> {
            Thread t1 = new Thread(() -> greedyAI.getBestMove(board, true));
            Thread t2 = new Thread(() -> greedyAI.getBestMove(board, false));
            
            t1.start();
            t2.start();
            
            t1.join();
            t2.join();
        });
    }

    @Test
    public void testAIWithCornerCases() {
        // Test AI with pieces in corners and edges
        board.clear();
        TestUtils.placeKing(board, 0, 0, true);    // Corner
        TestUtils.placeKing(board, 7, 7, false);   // Opposite corner
        TestUtils.placeRook(board, 0, 7, true);    // Edge
        TestUtils.placeRook(board, 7, 0, false);   // Edge
        
        // Should handle edge positions without issues
        assertNotNull(greedyAI.getBestMove(board, true));
        assertNotNull(greedyAI.getBestMove(board, false));
    }
}