package com.clewi.javachess.ai;

import com.clewi.javachess.model.Move;
import com.clewi.javachess.model.Board;

/**
 * Interface for all chess AI implementations.
 * Defines the contract that any AI must follow.
 */
public interface ChessAI {
    
    /**
     * Calculate and return the best move for the given board position.
     * 
     * @param board The current board state
     * @param isWhite Whether the AI is playing as white (true) or black (false)
     * @return The best move found, or null if no legal moves available
     */
    Move getBestMove(Board board, boolean isWhite);
    
    /**
     * Get the name of this AI implementation.
     * Used for debugging and display purposes.
     * 
     * @return A descriptive name for this AI
     */
    String getName();
}