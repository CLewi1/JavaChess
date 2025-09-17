package com.clewi.javachess.ai;

import com.clewi.javachess.model.Board;
import com.clewi.javachess.model.Move;
import com.clewi.javachess.pieces.Piece;
import com.clewi.javachess.util.DebugUtils;

import java.util.List;
import java.util.Random;

/**
 * Simple greedy AI that chooses moves based on immediate material gain.
 * 
 * This AI uses a 1-ply search (looks only at the immediate result of each move)
 * and picks the move that results in the best material balance.
 * 
 * Algorithm:
 * 1. Generate all legal moves
 * 2. For each move, simulate it and evaluate the resulting position
 * 3. Pick the move with the highest evaluation score
 * 4. If multiple moves have the same score, pick randomly among them
 */
public class GreedyAI implements ChessAI {
    
    private final Random random;
    
    public GreedyAI() {
        this.random = new Random();
    }
    
    @Override
    public Move getBestMove(Board board, boolean isWhite) {
        DebugUtils.logImportant("GreedyAI: Calculating best move for " + (isWhite ? "white" : "black"));
        
        // Initialize the move generator with the current board
        MoveGenerator generator = new MoveGenerator(board);
        
        // Generate all legal moves for this player
        List<Move> legalMoves = generator.getAllLegalMoves(isWhite);
        
        if (legalMoves.isEmpty()) {
            DebugUtils.logImportant("GreedyAI: No legal moves available!");
            return null; // No legal moves (checkmate or stalemate)
        }
        
        DebugUtils.logImportant("GreedyAI: Evaluating " + legalMoves.size() + " possible moves");
        
        // Find the best move by evaluating each one
        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        int movesWithBestScore = 0;
        
        for (Move move : legalMoves) {
            int score = evaluateMove(board, move, isWhite);
            
            DebugUtils.log("Move " + move + " scores " + score);
            
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
                movesWithBestScore = 1;
            } else if (score == bestScore) {
                // Multiple moves with same score - randomly pick among them for variety
                movesWithBestScore++;
                if (random.nextInt(movesWithBestScore) == 0) {
                    bestMove = move;
                }
            }
        }
        
        DebugUtils.logImportant("GreedyAI: Selected move " + bestMove + " with score " + bestScore);
        if (movesWithBestScore > 1) {
            DebugUtils.logImportant("GreedyAI: " + movesWithBestScore + " moves had the same score");
        }
        
        return bestMove;
    }
    
    /**
     * Evaluate a move by analyzing what material would be gained/lost.
     * This is a simplified but safe approach that doesn't require board copying.
     * 
     * @param board The current board state
     * @param move The move to evaluate
     * @param isWhite Whether the AI is playing as white
     * @return The evaluation score for this move (higher = better)
     */
    private int evaluateMove(Board board, Move move, boolean isWhite) {
        // Start with the current position's evaluation
        int baseScore = MaterialEvaluator.evaluatePosition(board, isWhite);
        
        // Calculate material change from this move
        int materialChange = 0;
        
        // If this move captures a piece, we gain its value
        Piece capturedPiece = board.getPiece(move.getDestination().x, move.getDestination().y);
        if (capturedPiece != null) {
            materialChange += MaterialEvaluator.calculateMaterialChange(board, capturedPiece);
        }
        
        // Handle en passant captures
        if (move.getMoveType().equals(com.clewi.javachess.model.MoveType.EN_PASSANT)) {
            Piece enPassantCaptured = board.getPiece(move.getDestination().x, move.getSource().y);
            if (enPassantCaptured != null) {
                materialChange += MaterialEvaluator.calculateMaterialChange(board, enPassantCaptured);
            }
        }
        
        // Calculate final score
        int finalScore = baseScore + materialChange;
        
        // Add small bonuses for certain move types to break ties intelligently
        finalScore += getMoveTypeBonus(move);
        
        return finalScore;
    }
    
    /**
     * Give small bonuses to certain types of moves to make the AI play more interestingly.
     * These bonuses are much smaller than piece values, so they only matter when
     * material gain is equal.
     */
    private int getMoveTypeBonus(Move move) {
        int bonus = 0;
        
        switch (move.getMoveType()) {
            case CAPTURE:
                // Slightly prefer captures (aggressive play)
                bonus += 10;
                break;
            case CASTLE:
                // Encourage castling for king safety
                bonus += 50;
                break;
            case PROMOTION:
                // Promote pawns when possible
                bonus += 20;
                break;
            case EN_PASSANT:
                // En passant is often a good tactical move
                bonus += 15;
                break;
            case NORMAL:
            default:
                // No bonus for normal moves
                break;
        }
        
        // Small random element to avoid always playing the same game
        bonus += random.nextInt(5);
        
        return bonus;
    }
    
    @Override
    public String getName() {
        return "GreedyAI";
    }
}