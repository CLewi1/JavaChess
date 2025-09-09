package com.clewi.javachess.game;

import com.clewi.javachess.model.*;
import com.clewi.javachess.pieces.*;
import com.clewi.javachess.testutils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.Point;

public class CastlingTest {
    private GameManager gameManager;
    private Board board;
    private MoveValidator moveValidator;

    @BeforeEach
    void setUp() {
        gameManager = new GameManager();
        board = TestUtils.emptyBoard();
        moveValidator = new MoveValidator(board);
    }

    @Test
    void testKingsideCastlingWhite() {
        // Set up white king and rook in starting positions
        King whiteKing = TestUtils.placeKing(board, 4, 7, true);
        Rook whiteRook = new Rook(7, 7, true, "Rook.png", board, false);
        whiteRook.setBoard(board);
        board.setSquare(7, 7, whiteRook);
        board.trackPiece(whiteRook);
        
        // Test kingside castling move (king moves from e1 to g1)
        Move castlingMove = new Move(new Point(4, 7), new Point(6, 7), whiteKing, MoveType.CASTLE);
        
        assertTrue(moveValidator.isValidMove(castlingMove), "Kingside castling should be valid");
        
        // Execute the move
        board.movePiece(castlingMove);
        
        // Verify king moved to g1
        assertEquals(whiteKing, board.getPiece(6, 7), "King should be on g1 after castling");
        assertNull(board.getPiece(4, 7), "e1 should be empty after castling");
        
        // Verify rook moved to f1
        assertEquals(whiteRook, board.getPiece(5, 7), "Rook should be on f1 after castling");
        assertNull(board.getPiece(7, 7), "h1 should be empty after castling");
        
        // Verify both pieces are marked as moved
        assertTrue(whiteKing.getHasMoved(), "King should be marked as moved");
        assertTrue(whiteRook.getHasMoved(), "Rook should be marked as moved");
    }

    @Test
    void testQueensideCastlingWhite() {
        // Set up white king and rook in starting positions
        King whiteKing = TestUtils.placeKing(board, 4, 7, true);
        Rook whiteRook = new Rook(0, 7, true, "Rook.png", board, false);
        whiteRook.setBoard(board);
        board.setSquare(0, 7, whiteRook);
        board.trackPiece(whiteRook);
        
        // Test queenside castling move (king moves from e1 to c1)
        Move castlingMove = new Move(new Point(4, 7), new Point(2, 7), whiteKing, MoveType.CASTLE);
        
        assertTrue(moveValidator.isValidMove(castlingMove), "Queenside castling should be valid");
        
        // Execute the move
        board.movePiece(castlingMove);
        
        // Verify king moved to c1
        assertEquals(whiteKing, board.getPiece(2, 7), "King should be on c1 after castling");
        assertNull(board.getPiece(4, 7), "e1 should be empty after castling");
        
        // Verify rook moved to d1
        assertEquals(whiteRook, board.getPiece(3, 7), "Rook should be on d1 after castling");
        assertNull(board.getPiece(0, 7), "a1 should be empty after castling");
    }

    @Test
    void testKingsideCastlingBlack() {
        // Set up black king and rook in starting positions
        King blackKing = TestUtils.placeKing(board, 4, 0, false);
        Rook blackRook = new Rook(7, 0, false, "Rook.png", board, false);
        blackRook.setBoard(board);
        board.setSquare(7, 0, blackRook);
        board.trackPiece(blackRook);
        
        // Test kingside castling move (king moves from e8 to g8)
        Move castlingMove = new Move(new Point(4, 0), new Point(6, 0), blackKing, MoveType.CASTLE);
        
        assertTrue(moveValidator.isValidMove(castlingMove), "Black kingside castling should be valid");
        
        // Execute the move
        board.movePiece(castlingMove);
        
        // Verify king moved to g8
        assertEquals(blackKing, board.getPiece(6, 0), "King should be on g8 after castling");
        
        // Verify rook moved to f8
        assertEquals(blackRook, board.getPiece(5, 0), "Rook should be on f8 after castling");
    }

    @Test
    void testCastlingFailsWhenKingHasMoved() {
        // Set up white king and rook
        King whiteKing = TestUtils.placeKing(board, 4, 7, true);
        whiteKing.setHasMoved(); // Mark king as moved
        Rook whiteRook = new Rook(7, 7, true, "Rook.png", board, false);
        whiteRook.setBoard(board);
        board.setSquare(7, 7, whiteRook);
        board.trackPiece(whiteRook);
        
        Move castlingMove = new Move(new Point(4, 7), new Point(6, 7), whiteKing, MoveType.CASTLE);
        
        assertFalse(moveValidator.isValidMove(castlingMove), "Castling should fail when king has moved");
    }

    @Test
    void testCastlingFailsWhenRookHasMoved() {
        // Set up white king and rook
        King whiteKing = TestUtils.placeKing(board, 4, 7, true);
        Rook whiteRook = new Rook(7, 7, true, "Rook.png", board, false);
        whiteRook.setHasMoved(); // Mark rook as moved
        whiteRook.setBoard(board);
        board.setSquare(7, 7, whiteRook);
        board.trackPiece(whiteRook);
        
        Move castlingMove = new Move(new Point(4, 7), new Point(6, 7), whiteKing, MoveType.CASTLE);
        
        assertFalse(moveValidator.isValidMove(castlingMove), "Castling should fail when rook has moved");
    }

    @Test
    void testCastlingFailsWhenPathIsBlocked() {
        // Set up white king and rook with bishop blocking the path
        King whiteKing = TestUtils.placeKing(board, 4, 7, true);
        Rook whiteRook = new Rook(7, 7, true, "Rook.png", board, false);
        whiteRook.setBoard(board);
        board.setSquare(7, 7, whiteRook);
        board.trackPiece(whiteRook);
        
        // Place a bishop blocking the castling path
        Bishop blockingBishop = new Bishop(5, 7, true, "Bishop.png", board, false);
        blockingBishop.setBoard(board);
        board.setSquare(5, 7, blockingBishop);
        board.trackPiece(blockingBishop);
        
        Move castlingMove = new Move(new Point(4, 7), new Point(6, 7), whiteKing, MoveType.CASTLE);
        
        assertFalse(moveValidator.isValidMove(castlingMove), "Castling should fail when path is blocked");
    }

    @Test
    void testCastlingFailsWhenKingIsInCheck() {
        // Set up white king and rook
        King whiteKing = TestUtils.placeKing(board, 4, 7, true);
        Rook whiteRook = new Rook(7, 7, true, "Rook.png", board, false);
        whiteRook.setBoard(board);
        board.setSquare(7, 7, whiteRook);
        board.trackPiece(whiteRook);
        
        // Place an enemy rook attacking the king
        Rook blackRook = new Rook(4, 0, false, "Rook.png", board, false);
        blackRook.setBoard(board);
        board.setSquare(4, 0, blackRook);
        board.trackPiece(blackRook);
        
        Move castlingMove = new Move(new Point(4, 7), new Point(6, 7), whiteKing, MoveType.CASTLE);
        
        assertFalse(moveValidator.isValidMove(castlingMove), "Castling should fail when king is in check");
    }
}
