package com.clewi.javachess.util;

import com.clewi.javachess.model.Board;
import com.clewi.javachess.pieces.Piece;

public class DebugUtils {
    
    // Set this to false to suppress verbose debug messages
    public static final boolean DEBUG_MODE = true;
    
    // Set this to false to suppress pawn movement debug messages specifically
    public static final boolean DEBUG_PAWN_MOVES = false;
    
    // Set this to false to suppress board state printouts
    public static final boolean DEBUG_BOARD_STATE = false;
    
    // Set this to false to suppress move simulation debug messages
    public static final boolean DEBUG_MOVE_SIMULATION = false;
    
    // Flag used to indicate if we're in simulation mode (for validation)
    private static boolean simulationMode = false;
    
    public static void enterSimulationMode() {
        simulationMode = true;
    }
    
    public static void exitSimulationMode() {
        simulationMode = false;
    }
    
    public static boolean isInSimulationMode() {
        return simulationMode;
    }
    
    public static void log(String message) {
        if (DEBUG_MODE && (!simulationMode || DEBUG_MOVE_SIMULATION)) {
            System.out.println(message);
        }
    }
    
    public static void logPawnMove(String message) {
        if (DEBUG_MODE && DEBUG_PAWN_MOVES && (!simulationMode || DEBUG_MOVE_SIMULATION)) {
            System.out.println(message);
        }
    }
    
    public static void logImportant(String message) {
        // Always log important messages when not in simulation
        if (!simulationMode) {
            System.out.println(message);
        } else if (DEBUG_MODE && DEBUG_MOVE_SIMULATION) {
            // In simulation, only log if debug enabled
            System.out.println("[SIM] " + message);
        }
    }
    
    public static void printBoard(Board board) {
        if (DEBUG_MODE && DEBUG_BOARD_STATE && (!simulationMode || DEBUG_MOVE_SIMULATION)) {
            System.out.println("Current Board State:");
            for (int y = 0; y < 8; y++) {
                for (int x = 0; x < 8; x++) {
                    Piece p = board.getPiece(x, y);
                    String symbol = ".";
                    if (p != null) {
                        symbol = (p.isWhite() ? "w" : "b") + p.getClass().getSimpleName().charAt(0);
                    }
                    System.out.print(symbol + " ");
                }
                System.out.println();
            }
        }
    }
}
