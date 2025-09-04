package com.clewi.javachess.game;

import static org.junit.jupiter.api.Assertions.*;

import com.clewi.javachess.model.Board;
import com.clewi.javachess.model.Move;
import com.clewi.javachess.model.MoveType;
import com.clewi.javachess.pieces.Pawn;
import com.clewi.javachess.pieces.Knight;
import com.clewi.javachess.pieces.King;
import com.clewi.javachess.testutils.TestUtils;
import org.junit.jupiter.api.Test;
import java.awt.Point;

public class MoveValidatorTest {

    @Test
    public void testPawnOneStepForward() {
        // Test: a white pawn should be able to move one square forward when
        // unobstructed
        Board board = TestUtils.emptyBoard();
        Pawn pawn = TestUtils.placePawn(board, 4, 6, true); // white pawn (starts at row 6)
        Move move = new Move(new Point(4, 6), new Point(4, 5), pawn, MoveType.NORMAL);
        MoveValidator validator = new MoveValidator(board);
        assertTrue(validator.isValidMove(move));
    }

    @Test
    public void testPawnCaptureDiagonal() {
        // Test: pawn can capture diagonally when an opposing piece is present
        Board board = TestUtils.emptyBoard();
        Pawn white = TestUtils.placePawn(board, 4, 4, true);
        TestUtils.placePawn(board, 5, 3, false);
        Move move = new Move(new Point(4, 4), new Point(5, 3), white, MoveType.CAPTURE);
        MoveValidator validator = new MoveValidator(board);
        assertTrue(validator.isValidMove(move));
    }

    @Test
    public void testKnightLShape() {
        // Test: knight moves in L-shape and can jump over pieces
        Board board = TestUtils.emptyBoard();
        Knight knight = TestUtils.placeKnight(board, 1, 0, true);
        Move move = new Move(new Point(1, 0), new Point(2, 2), knight, MoveType.NORMAL);
        MoveValidator validator = new MoveValidator(board);
        assertTrue(validator.isValidMove(move));
    }

    @Test
    public void testSimpleCheckDetection() {
        // Test: a king is detected as in check when an opposing piece attacks its
        // square
        Board board = TestUtils.emptyBoard();
        TestUtils.placeKing(board, 4, 4, true);
        TestUtils.placeQueen(board, 4, 0, false);
        MoveValidator validator = new MoveValidator(board);
        assertTrue(validator.isKingInCheck(true));
    }
}
