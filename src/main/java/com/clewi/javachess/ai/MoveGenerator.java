package com.clewi.javachess.ai;

import com.clewi.javachess.model.Board;
import com.clewi.javachess.model.Move;
import com.clewi.javachess.model.MoveType;
import com.clewi.javachess.game.MoveValidator;
import com.clewi.javachess.pieces.Piece;
import com.clewi.javachess.pieces.Pawn;
import com.clewi.javachess.pieces.King;
import com.clewi.javachess.util.DebugUtils;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for generating legal moves for AI analysis.
 * This class handles the complex logic of finding all legal moves for a given position.
 */
public class MoveGenerator {
    
    private final Board board;
    private final MoveValidator moveValidator;
    
    public MoveGenerator(Board board) {
        this.board = board;
        this.moveValidator = new MoveValidator(board);
    }
    
    /**
     * Generate all legal moves for the specified player.
     * This is the core method that the AI will use to evaluate positions.
     * 
     * @param isWhite Whether to generate moves for white (true) or black (false)
     * @return List of all legal moves available to the player
     */
    public List<Move> getAllLegalMoves(boolean isWhite) {
        List<Move> legalMoves = new ArrayList<>();
        
        // Get all pieces for the current player
        List<Piece> pieces = board.getPieces(isWhite);
        
        for (Piece piece : pieces) {
            // Skip captured pieces
            if (piece.isCaptured()) {
                continue;
            }
            
            // Generate all possible moves for this piece
            List<Move> pieceMoves = generateMovesForPiece(piece);
            legalMoves.addAll(pieceMoves);
        }
        
        DebugUtils.log("Generated " + legalMoves.size() + " legal moves for " + 
                      (isWhite ? "white" : "black"));
        
        return legalMoves;
    }
    
    /**
     * Generate all legal moves for a specific piece.
     * This method considers all squares on the board and validates each potential move.
     * 
     * @param piece The piece to generate moves for
     * @return List of legal moves for this piece
     */
    private List<Move> generateMovesForPiece(Piece piece) {
        List<Move> moves = new ArrayList<>();
        Point sourcePos = new Point(piece.getX(), piece.getY());
        
        // Check every square on the board as a potential destination
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Point destPos = new Point(x, y);
                
                // Skip if trying to move to the same square
                if (sourcePos.equals(destPos)) {
                    continue;
                }
                
                // Determine the move type based on the destination and piece
                MoveType moveType = determineMoveType(piece, sourcePos, destPos);
                
                // Create the move based on the type
                Move move = createMove(sourcePos, destPos, piece, moveType);
                
                // Validate the move using the existing MoveValidator
                if (moveValidator.isValidMove(move)) {
                    moves.add(move);
                }
            }
        }
        
        return moves;
    }
    
    /**
     * Determine the type of move based on the piece and destination.
     * This helps classify moves as normal, capture, castling, etc.
     */
    private MoveType determineMoveType(Piece piece, Point source, Point dest) {
        Piece destPiece = board.getPiece(dest.x, dest.y);
        
        // Check for en passant (pawn captures diagonally to empty square)
        if (piece instanceof Pawn) {
            int direction = piece.isWhite() ? -1 : 1;
            if (Math.abs(dest.x - source.x) == 1 && dest.y == source.y + direction && destPiece == null) {
                Piece adjacentPiece = board.getPiece(dest.x, source.y);
                if (adjacentPiece instanceof Pawn && 
                    adjacentPiece.isWhite() != piece.isWhite() &&
                    board.isEnPassantPossible((Pawn) adjacentPiece)) {
                    return MoveType.EN_PASSANT;
                }
            }
            
            // Check for promotion (pawn reaching the end)
            if ((piece.isWhite() && dest.y == 0) || (!piece.isWhite() && dest.y == 7)) {
                return MoveType.PROMOTION;
            }
        }
        
        // Check for castling (king moves 2 squares horizontally)
        if (piece instanceof King) {
            if (Math.abs(dest.x - source.x) == 2 && dest.y == source.y) {
                return MoveType.CASTLE;
            }
        }
        
        // Normal capture or move
        return destPiece != null ? MoveType.CAPTURE : MoveType.NORMAL;
    }
    
    /**
     * Create a Move object based on the move type.
     * Some moves (like promotion) require special handling.
     */
    private Move createMove(Point source, Point dest, Piece piece, MoveType moveType) {
        if (moveType == MoveType.PROMOTION) {
            // For AI purposes, always promote to queen (strongest piece)
            return new Move(source, dest, piece, moveType, "Queen");
        } else {
            return new Move(source, dest, piece, moveType);
        }
    }
}