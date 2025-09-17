package com.clewi.javachess.game;

import com.clewi.javachess.model.*;
import com.clewi.javachess.pieces.*;
import com.clewi.javachess.testutils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.Point;

public class PromotionTest {
    private Board board;
    private MoveValidator moveValidator;

    @BeforeEach
    void setUp() {
        board = TestUtils.emptyBoard();
        // Place kings so validators that check for check have kings to find
        TestUtils.placeKing(board, 4, 7, true);  // White king on e1
        TestUtils.placeKing(board, 4, 0, false); // Black king on e8
        moveValidator = new MoveValidator(board);
    }

    @Test
    void testStraightPromotionOptionsAndMoves() {
        // Place a white pawn ready to promote at g7 -> g8
        Pawn pawn = TestUtils.placePawn(board, 6, 1, true); // g7

        String[] options = {"Queen", "Rook", "Bishop", "Knight"};
        for (String choice : options) {
            // Reset board between iterations
            board.clear();
            TestUtils.placeKing(board, 4, 7, true);
            TestUtils.placeKing(board, 4, 0, false);
            pawn = TestUtils.placePawn(board, 6, 1, true);

            Move promote = new Move(new Point(6, 1), new Point(6, 0), pawn, MoveType.PROMOTION, choice);
            assertTrue(moveValidator.isValidMove(promote), "Promotion to " + choice + " should be valid");
            board.movePiece(promote);

            Piece promoted = board.getPiece(6, 0);
            assertNotNull(promoted, "Promoted piece must exist");

            switch (choice.toLowerCase()) {
                case "queen":
                    assertTrue(promoted instanceof Queen);
                    // Try a queen move: g8 to g5 (vertical down) - ensure path clear
                    Move queenMove = new Move(new Point(6,0), new Point(6,3), promoted, MoveType.NORMAL);
                    assertTrue(moveValidator.isValidMove(queenMove));
                    board.movePiece(queenMove);
                    assertEquals(promoted, board.getPiece(6,3));
                    break;
                case "rook":
                    assertTrue(promoted instanceof Rook);
                    // Rook: g8 to g6
                    Move rookMove = new Move(new Point(6,0), new Point(6,2), promoted, MoveType.NORMAL);
                    assertTrue(moveValidator.isValidMove(rookMove));
                    board.movePiece(rookMove);
                    assertEquals(promoted, board.getPiece(6,2));
                    break;
                case "bishop":
                    assertTrue(promoted instanceof Bishop);
                    // Bishop: g8 to e6 (diagonal)
                    Move bishopMove = new Move(new Point(6,0), new Point(4,2), promoted, MoveType.NORMAL);
                    assertTrue(moveValidator.isValidMove(bishopMove));
                    board.movePiece(bishopMove);
                    assertEquals(promoted, board.getPiece(4,2));
                    break;
                case "knight":
                    assertTrue(promoted instanceof Knight);
                    // Knight: g8 to e7 (L-shape)
                    Move knightMove = new Move(new Point(6,0), new Point(4,1), promoted, MoveType.NORMAL);
                    assertTrue(moveValidator.isValidMove(knightMove));
                    board.movePiece(knightMove);
                    assertEquals(promoted, board.getPiece(4,1));
                    break;
            }
        }
    }

    @Test
    void testPromotionThatLeavesOwnKingInCheckIsInvalid() {
        // White king on a1, pawn on a2 blocking a-file rook at a8
        board.clear();
        TestUtils.placeKing(board, 0, 7, true); // a1
        TestUtils.placeKing(board, 4, 0, false); // black king somewhere safe
        Pawn pawn = TestUtils.placePawn(board, 0, 6, true); // a2
        // Place an enemy piece to be captured on b1 (so pawn can capture off-file)
        Rook blackRook = new Rook(0,0,false,"Rook.png",board,false); // rook at a8
        blackRook.setBoard(board);
        board.setSquare(0,0, blackRook);
        board.trackPiece(blackRook);
        // A black piece on b1 to allow capture into promotion
        Knight blackKnight = new Knight(1,7,false,"Knight.png",board,false); // b1
        blackKnight.setBoard(board);
        board.setSquare(1,7, blackKnight);
        board.trackPiece(blackKnight);

        // Pawn captures from a2 to b1 promoting; this vacates a2 so rook on a8 would then attack king on a1
        Move promoteCapture = new Move(new Point(0,6), new Point(1,7), pawn, MoveType.PROMOTION, "Queen");
        assertFalse(moveValidator.isValidMove(promoteCapture), "Promotion that leaves own king in check should be invalid");
    }

    @Test
    void testPromotionThatGivesCheckToOpponent() {
        // White pawn g7 -> g8 promotes to queen giving check to black king on h8
        board.clear();
        TestUtils.placeKing(board, 4, 7, true); // white king safe
        TestUtils.placeKing(board, 7, 0, false); // black king on h8
        Pawn pawn = TestUtils.placePawn(board, 6, 1, true); // g7

        Move promote = new Move(new Point(6,1), new Point(6,0), pawn, MoveType.PROMOTION, "Queen");
        assertTrue(moveValidator.isValidMove(promote));
        board.movePiece(promote);

        // After promotion, queen on g8 should attack h8 (adjacent rank)
        assertTrue(moveValidator.isKingInCheck(false), "Black king should be in check after promotion to queen on g8");
    }

    @Test
    void testCaptureIntoPromotion() {
        // White pawn captures on h8 and promotes
        board.clear();
        TestUtils.placeKing(board, 4, 7, true);
        TestUtils.placeKing(board, 4, 0, false);

        Pawn pawn = TestUtils.placePawn(board, 6, 1, true); // g7
        // Place black rook on h8 to be captured
        Rook blackRook = new Rook(7,0,false,"Rook.png",board,false);
        blackRook.setBoard(board);
        board.setSquare(7,0,blackRook);
        board.trackPiece(blackRook);

        Move capturePromote = new Move(new Point(6,1), new Point(7,0), pawn, MoveType.PROMOTION, "Knight");
        assertTrue(moveValidator.isValidMove(capturePromote));
        board.movePiece(capturePromote);

        Piece promoted = board.getPiece(7,0);
        assertTrue(promoted instanceof Knight, "Promotion capturing should result in chosen piece present on destination");
    }
}
