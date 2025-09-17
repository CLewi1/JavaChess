package com.clewi.javachess.ai;

import static org.junit.jupiter.api.Assertions.*;

import com.clewi.javachess.model.Board;
import com.clewi.javachess.pieces.*;
import com.clewi.javachess.testutils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MaterialEvaluatorTest {
    private Board board;

    @BeforeEach
    public void setUp() {
        board = TestUtils.emptyBoard();
    }

    @Test
    public void testPieceValues() {
        // Test individual piece values match expected constants
        assertEquals(100, MaterialEvaluator.getPieceValue(new Pawn(0, 0, true, "Pawn.png", board, false)));
        assertEquals(300, MaterialEvaluator.getPieceValue(new Knight(0, 0, true, "Knight.png", board, false)));
        assertEquals(320, MaterialEvaluator.getPieceValue(new Bishop(0, 0, true, "Bishop.png", board, false)));
        assertEquals(500, MaterialEvaluator.getPieceValue(new Rook(0, 0, true, "Rook.png", board, false)));
        assertEquals(900, MaterialEvaluator.getPieceValue(new Queen(0, 0, true, "Queen.png", board, false)));
        assertEquals(10000, MaterialEvaluator.getPieceValue(new King(0, 0, true, "King.png", board, false)));
    }

    @Test
    public void testEmptyBoardEvaluation() {
        // Empty board should evaluate to 0 for both colors
        assertEquals(0, MaterialEvaluator.evaluatePosition(board, true));
        assertEquals(0, MaterialEvaluator.evaluatePosition(board, false));
    }

    @Test
    public void testSinglePieceEvaluation() {
        // Single white pawn
        TestUtils.placePawn(board, 4, 4, true);
        assertEquals(100, MaterialEvaluator.evaluatePosition(board, true));   // White perspective: +100
        assertEquals(-100, MaterialEvaluator.evaluatePosition(board, false)); // Black perspective: -100
        
        // Reset and test black pawn
        board = TestUtils.emptyBoard();
        TestUtils.placePawn(board, 4, 4, false);
        assertEquals(-100, MaterialEvaluator.evaluatePosition(board, true));  // White perspective: -100
        assertEquals(100, MaterialEvaluator.evaluatePosition(board, false));  // Black perspective: +100
    }

    @Test
    public void testBalancedPosition() {
        // Equal material should evaluate to 0
        TestUtils.placePawn(board, 1, 1, true);   // +100
        TestUtils.placePawn(board, 1, 6, false);  // -100
        TestUtils.placeKnight(board, 2, 1, true); // +300
        TestUtils.placeKnight(board, 2, 6, false); // -300
        
        assertEquals(0, MaterialEvaluator.evaluatePosition(board, true));
        assertEquals(0, MaterialEvaluator.evaluatePosition(board, false));
    }

    @Test
    public void testWhiteMaterialAdvantage() {
        // White has queen, black has rook - white should be ahead by 400
        TestUtils.placeQueen(board, 3, 0, true);  // +900
        TestUtils.placeRook(board, 3, 7, false);  // -500
        
        assertEquals(400, MaterialEvaluator.evaluatePosition(board, true));   // White perspective: +400
        assertEquals(-400, MaterialEvaluator.evaluatePosition(board, false)); // Black perspective: -400
    }

    @Test
    public void testBlackMaterialAdvantage() {
        // Black has two knights, white has one bishop - black ahead by 280
        TestUtils.placeKnight(board, 1, 7, false); // -300
        TestUtils.placeKnight(board, 6, 7, false); // -300
        TestUtils.placeBishop(board, 2, 0, true);   // +320
        
        assertEquals(-280, MaterialEvaluator.evaluatePosition(board, true));  // White perspective: -280
        assertEquals(280, MaterialEvaluator.evaluatePosition(board, false));  // Black perspective: +280
    }

    @Test
    public void testComplexPosition() {
        // Build a more complex position and verify evaluation
        // White pieces
        TestUtils.placeKing(board, 4, 0, true);     // +10000
        TestUtils.placeQueen(board, 3, 0, true);    // +900
        TestUtils.placeRook(board, 0, 0, true);     // +500
        TestUtils.placeRook(board, 7, 0, true);     // +500
        TestUtils.placeBishop(board, 2, 0, true);   // +320
        TestUtils.placeBishop(board, 5, 0, true);   // +320
        TestUtils.placeKnight(board, 1, 0, true);   // +300
        TestUtils.placeKnight(board, 6, 0, true);   // +300
        // 8 pawns
        for (int i = 0; i < 8; i++) {
            TestUtils.placePawn(board, i, 1, true); // +800
        }
        
        // Black pieces (same setup)
        TestUtils.placeKing(board, 4, 7, false);    
        TestUtils.placeQueen(board, 3, 7, false);   
        TestUtils.placeRook(board, 0, 7, false);    
        TestUtils.placeRook(board, 7, 7, false);    
        TestUtils.placeBishop(board, 2, 7, false);  
        TestUtils.placeBishop(board, 5, 7, false);  
        TestUtils.placeKnight(board, 1, 7, false);  
        TestUtils.placeKnight(board, 6, 7, false);  
        for (int i = 0; i < 8; i++) {
            TestUtils.placePawn(board, i, 6, false);
        }
        
        // Starting position should evaluate to 0 (equal material)
        assertEquals(0, MaterialEvaluator.evaluatePosition(board, true));
        assertEquals(0, MaterialEvaluator.evaluatePosition(board, false));
    }

    @Test
    public void testNullPieceHandling() {
        // Test that evaluator handles null pieces gracefully
        assertEquals(0, MaterialEvaluator.getPieceValue(null));
    }

    @Test
    public void testMaterialChangeCalculation() {
        // Test material change calculation for captures
        Queen queen = TestUtils.placeQueen(board, 0, 0, true);
        Rook rook = TestUtils.placeRook(board, 1, 0, false);
        
        // Capturing a queen should give +900 points
        assertEquals(900, MaterialEvaluator.calculateMaterialChange(board, queen));
        
        // Capturing a rook should give +500 points
        assertEquals(500, MaterialEvaluator.calculateMaterialChange(board, rook));
        
        // No capture should give 0 points
        assertEquals(0, MaterialEvaluator.calculateMaterialChange(board, null));
    }

    @Test
    public void testMaterialBalanceDescription() {
        // Test the description method
        assertEquals("Material is equal", MaterialEvaluator.getMaterialBalanceDescription(board));
        
        // Add white advantage
        TestUtils.placeQueen(board, 0, 0, true);
        String description = MaterialEvaluator.getMaterialBalanceDescription(board);
        assertTrue(description.contains("White leads by 900 points"));
        
        // Reset and add black advantage
        board = TestUtils.emptyBoard();
        TestUtils.placeRook(board, 0, 0, false);
        description = MaterialEvaluator.getMaterialBalanceDescription(board);
        assertTrue(description.contains("Black leads by 500 points"));
    }

    @Test
    public void testEdgeCasePositions() {
        // King vs King endgame
        TestUtils.placeKing(board, 4, 4, true);
        TestUtils.placeKing(board, 4, 6, false);
        assertEquals(0, MaterialEvaluator.evaluatePosition(board, true)); // Equal kings cancel out
        assertEquals(0, MaterialEvaluator.evaluatePosition(board, false));
        
        // King and pawn vs King
        board = TestUtils.emptyBoard();
        TestUtils.placeKing(board, 4, 4, true);
        TestUtils.placeKing(board, 4, 6, false);
        TestUtils.placePawn(board, 3, 4, true);
        assertEquals(100, MaterialEvaluator.evaluatePosition(board, true));  // White ahead by pawn
        assertEquals(-100, MaterialEvaluator.evaluatePosition(board, false)); // Black behind by pawn
    }
}