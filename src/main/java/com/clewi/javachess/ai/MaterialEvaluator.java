package com.clewi.javachess.ai;

import com.clewi.javachess.model.Board;
import com.clewi.javachess.pieces.*;

/**
 * Simple material evaluation for chess positions.
 * Uses standard piece values to evaluate how good a position is based on material balance.
 * 
 * This is the foundation for our greedy AI algorithm.
 */
public class MaterialEvaluator {
    
    // Standard piece values in points
    private static final int PAWN_VALUE = 100;    // Base unit
    private static final int KNIGHT_VALUE = 300;  // Slightly more than 3 pawns
    private static final int BISHOP_VALUE = 320;  // Slightly better than knight in open positions  
    private static final int ROOK_VALUE = 500;    // 5 pawns
    private static final int QUEEN_VALUE = 900;   // 9 pawns
    private static final int KING_VALUE = 10000;  // Invaluable (but game ends if captured)
    
    /**
     * Evaluate the current board position from the perspective of the specified player.
     * Positive scores favor the player, negative scores favor the opponent.
     * 
     * @param board The board to evaluate
     * @param isWhite Whether to evaluate from white's perspective (true) or black's (false)
     * @return The evaluation score (positive = good for player, negative = bad for player)
     */
    public static int evaluatePosition(Board board, boolean isWhite) {
        int whiteScore = calculateMaterialValue(board, true);
        int blackScore = calculateMaterialValue(board, false);
        
        // Return score from the perspective of the current player
        if (isWhite) {
            return whiteScore - blackScore;
        } else {
            return blackScore - whiteScore;
        }
    }
    
    /**
     * Calculate the total material value for one side.
     * 
     * @param board The board to analyze
     * @param isWhite Whether to calculate for white (true) or black (false)
     * @return Total point value of all pieces for this side
     */
    private static int calculateMaterialValue(Board board, boolean isWhite) {
        int totalValue = 0;
        
        // Get all pieces for the specified color
        for (Piece piece : board.getPieces(isWhite)) {
            // Skip captured pieces
            if (piece.isCaptured()) {
                continue;
            }
            
            totalValue += getPieceValue(piece);
        }
        
        return totalValue;
    }
    
    /**
     * Get the point value of a specific piece.
     * 
     * @param piece The piece to evaluate
     * @return The point value of this piece type
     */
    public static int getPieceValue(Piece piece) {
        if (piece instanceof Pawn) {
            return PAWN_VALUE;
        } else if (piece instanceof Knight) {
            return KNIGHT_VALUE;
        } else if (piece instanceof Bishop) {
            return BISHOP_VALUE;
        } else if (piece instanceof Rook) {
            return ROOK_VALUE;
        } else if (piece instanceof Queen) {
            return QUEEN_VALUE;
        } else if (piece instanceof King) {
            return KING_VALUE;
        } else {
            // Unknown piece type - shouldn't happen
            return 0;
        }
    }
    
    /**
     * Calculate how much material would be gained/lost by making a move.
     * This is useful for quickly evaluating captures without full position analysis.
     * 
     * @param board The current board
     * @param move The move being considered (not used yet, but will be for future enhancements)
     * @param capturedPiece The piece that would be captured (null if no capture)
     * @return The material change (positive = material gained, negative = material lost)
     */
    public static int calculateMaterialChange(Board board, Piece capturedPiece) {
        if (capturedPiece == null) {
            return 0; // No material change
        }
        
        // Gain the value of the captured piece
        return getPieceValue(capturedPiece);
    }
    
    /**
     * Get a human-readable description of the current material balance.
     * Useful for debugging and understanding what the AI is thinking.
     * 
     * @param board The board to analyze
     * @return A string describing the material balance
     */
    public static String getMaterialBalanceDescription(Board board) {
        int whiteValue = calculateMaterialValue(board, true);
        int blackValue = calculateMaterialValue(board, false);
        int difference = whiteValue - blackValue;
        
        if (difference > 0) {
            return "White leads by " + difference + " points";
        } else if (difference < 0) {
            return "Black leads by " + Math.abs(difference) + " points";
        } else {
            return "Material is equal";
        }
    }
}