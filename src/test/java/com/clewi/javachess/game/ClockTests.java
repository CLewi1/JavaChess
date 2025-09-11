package com.clewi.javachess.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.clewi.javachess.model.*;
import com.clewi.javachess.pieces.Pawn;
import com.clewi.javachess.testutils.TestUtils;
import java.awt.Point;

public class ClockTests {
    private GameManager gameManager;
    private Board board;
    
    @BeforeEach
    void setUp() {
        gameManager = new GameManager();
    }

    @Test
    public void testClocksStartPaused() {
        // Clocks should not be running until the first move is made
        gameManager.initClocks(5, 2);

        assertFalse(gameManager.areClocksRunning(), "Clocks should start paused");
    }

    @Test
    public void testClocksStartRunningAfterFirstMove() {
        gameManager.initClocks(5, 2);

        // Simulate first move
        board = TestUtils.emptyBoard();
        Pawn pawn = TestUtils.placePawn(board, 4, 6, true); // white pawn (starts at row 6)
        gameManager.makeMove(new Move(new Point(4, 6), new Point(4, 5), pawn, MoveType.NORMAL));

        // Clocks should start running after the first move
        assertTrue(gameManager.areClocksRunning(), "Clocks should start running after the first move");
    }

    @Test
    public void testMoveTimeIncrement() {
        gameManager.initClocks(300, 2);

        Integer initialWhiteTime = gameManager.getWhiteSecondsRemaining();

        board = TestUtils.emptyBoard();
        Pawn whitePawn = TestUtils.placePawn(board, 4, 6, true); // white pawn
        gameManager.makeMove(new Move(new Point(4, 6), new Point(4, 5), whitePawn, MoveType.NORMAL));

        assertEquals(initialWhiteTime + 2, gameManager.getWhiteSecondsRemaining());
    }

    @Test
    public void testClockDecrement() {
        gameManager.initClocks(300, 2);

        int initialWhiteTime = gameManager.getWhiteSecondsRemaining();
        int initialBlackTime = gameManager.getBlackSecondsRemaining();

        // Simulate first move
        board = TestUtils.emptyBoard();
        Pawn pawn = TestUtils.placePawn(board, 4, 6, true); // white pawn (starts at row 6)
        gameManager.makeMove(new Move(new Point(4, 6), new Point(4, 5), pawn, MoveType.NORMAL));



        // Simulate 5 seconds passing
        for (int i = 0; i < 5; i++) {
            gameManager.tick();
        }

        // Simulate black move
        Pawn blackPawn = TestUtils.placePawn(board, 4, 1, false);
        gameManager.makeMove(new Move(new Point(4, 1), new Point(4, 2), blackPawn, MoveType.NORMAL));

        int finalWhiteTime = gameManager.getWhiteSecondsRemaining();
        int finalBlackTime = gameManager.getBlackSecondsRemaining();

        // Check that the clocks have decremented correctly
        assertEquals(initialWhiteTime + 2, finalWhiteTime);
        assertEquals(initialBlackTime - 3, finalBlackTime);
    }

    @Test
    public void testTimeoutDetection() {
        gameManager.initClocks(2, 0);
        board = TestUtils.emptyBoard();
        
        // Simulate first move
        Pawn pawn = TestUtils.placePawn(board, 4, 6, true); // white pawn (starts at row 6)
        gameManager.makeMove(new Move(new Point(4, 6), new Point(4, 5), pawn, MoveType.NORMAL));
        assertTrue(gameManager.areClocksRunning(), "Clocks should be running after first move");

        // Simulate 3 seconds passing to trigger timeout
        for (int i = 0; i < 3; i++) {
            gameManager.tick();
        }

        assertEquals(GameState.TIMEOUT, gameManager.getGameState(), "Game state should be TIMEOUT after clock expires");
    }

    @Test 
    public void testClocksDisabled() {
        assertFalse(gameManager.isClockEnabled(), "Clocks should be disabled by default");
        assertFalse(gameManager.tick(), "Tick should return false when clocks are disabled");
    }

    @Test
    public void testInvalidMoveDoesNotStartClocks() {
        GameManager gm = new GameManager();
        gm.initClocks(300, 2);
        Board board = TestUtils.emptyBoard();
        Pawn wp = TestUtils.placePawn(board, 4, 6, true);

        // attempt an illegal move (backwards for white) -> should be rejected
        Move illegal = new Move(new Point(4, 6), new Point(4, 7), wp, MoveType.NORMAL);
        gm.makeMove(illegal);

        assertFalse(gm.areClocksRunning(), "illegal move should not start clocks");
    }

    @Test
    public void testNewGameResetsClocks() {
        gameManager.initClocks(300, 2);
        board = TestUtils.emptyBoard();
        Pawn pawn = TestUtils.placePawn(board, 4, 6, true); // white pawn (starts at row 6)
        gameManager.makeMove(new Move(new Point(4, 6), new Point(4, 5), pawn, MoveType.NORMAL));
        assertTrue(gameManager.areClocksRunning(), "Clocks should be running after first move");

        // Simulate a new game starting
        gameManager = new GameManager();
        gameManager.initClocks(300, 2);
        assertFalse(gameManager.areClocksRunning(), "Clocks should be reset and paused after new game");
        assertEquals(300, gameManager.getWhiteSecondsRemaining(), "White clock should reset to initial time");
        assertEquals(300, gameManager.getBlackSecondsRemaining(), "Black clock should reset to initial time");

    }

    @Test
    public void testUndoMoveResetsClock() {
        gameManager.initClocks(300, 2);
        board = TestUtils.emptyBoard();

        // Simulate first move
        Pawn whitePawn = TestUtils.placePawn(board, 4, 6, true); // white pawn
        gameManager.makeMove(new Move(new Point(4, 6), new Point(4, 5), whitePawn, MoveType.NORMAL));
        assertTrue(gameManager.areClocksRunning(), "Clocks should be running after first move");

        // Simulate 5 seconds passing
        for (int i = 0; i < 5; i++) {
            gameManager.tick();
        }

        // Simulate black move
        Pawn blackPawn = TestUtils.placePawn(board, 4, 1, false);
        gameManager.makeMove(new Move(new Point(4, 1), new Point(4, 2), blackPawn, MoveType.NORMAL));

        int blackTimeAfterTicks = gameManager.getBlackSecondsRemaining();
        assertEquals(297, blackTimeAfterTicks, "Black clock should have decremented by 3 seconds");

        // Undo the move
        gameManager.undoMove();

        // After undo, clocks should be paused and black time should reset to before the move
        assertTrue(gameManager.areClocksRunning(), "Clocks should be running after undo");
        assertEquals(300, gameManager.getBlackSecondsRemaining(), "Black clock should reset to time before the undone move");

    }


}


