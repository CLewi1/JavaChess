package com.clewi.javachess.game;

import com.clewi.javachess.model.*;
import com.clewi.javachess.pieces.*;
import com.clewi.javachess.util.DebugUtils;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class MoveValidator {
    private final Board board;
    
    public MoveValidator(Board board) {
        this.board = board;
    }
    
    public boolean isValidMove(Move move) {
        if (move == null || move.getPiece() == null || board == null) {
            return false;
        }
        
        Piece piece = move.getPiece();
        Point source = move.getSource();
        Point dest = move.getDestination();
        
        // Make sure the piece has a reference to the board
        if (piece.getBoard() == null) {
            piece.setBoard(board);
        }
        
        // Check if the destination is within bounds
        if (!isWithinBounds(dest.x, dest.y)) {
            return false;
        }
        
        // Check if the piece can move to the destination according to chess rules
        if (!piece.canMove(dest.x, dest.y)) {
            return false;
        }
        
        // Capture check: Print debug message if this is a capture attempt
        Piece targetPiece = board.getPiece(dest);
        if (targetPiece != null) {
            DebugUtils.logImportant("Attempting to capture " + targetPiece.getClass().getSimpleName());
        }
        
        // Is the king currently in check?
        boolean kingInCheck = isKingInCheck(piece.isWhite());
        DebugUtils.logImportant("King is " + (kingInCheck ? "" : "not ") + "in check");
        
        // If king is in check, we must verify this move resolves the check
        if (kingInCheck) {
            return wouldMoveResolveCheck(move);
        }
        
        // If king is not in check, make sure this move doesn't put it in check
        boolean wouldLeaveInCheck = wouldLeaveKingInCheck(move);
        return !wouldLeaveInCheck;
    }
    
    private boolean isWithinBounds(int x, int y) {
        return x >= 0 && x <= 7 && y >= 0 && y <= 7;
    }
    
    private boolean wouldLeaveKingInCheck(Move move) {
        // Save the current state
        Piece piece = move.getPiece();
        Point source = move.getSource();
        Point dest = move.getDestination();
        Piece capturedPiece = board.getPiece(dest);
        Piece enPassantCapturedPiece = null;
        
        // Handle en passant capture during simulation
        if (move.getMoveType() == MoveType.EN_PASSANT) {
            enPassantCapturedPiece = board.getPiece(dest.x, source.y);
        }
        
        // Enter simulation mode to suppress unnecessary logs
        DebugUtils.enterSimulationMode();
        
        // Simulate the move
        board.updatePiecePosition(piece, source, dest);
        
        // Simulate en passant capture if applicable
        if (enPassantCapturedPiece != null) {
            board.setSquare(dest.x, source.y, null); // Remove captured pawn
            enPassantCapturedPiece.setCaptured(true);
        }
        
        // Check if king is in check after the move
        boolean kingInCheck = isKingInCheck(piece.isWhite());
        
        // Restore the board state
        board.undoMove(piece, source, dest, capturedPiece);
        
        // Restore en passant captured piece
        if (enPassantCapturedPiece != null) {
            board.setSquare(dest.x, source.y, enPassantCapturedPiece);
            enPassantCapturedPiece.setCaptured(false);
        }
        
        // Exit simulation mode
        DebugUtils.exitSimulationMode();
        
        if (kingInCheck) {
            DebugUtils.log("Move would leave/put king in check");
        }
        
        return kingInCheck;
    }
    
    public boolean isKingExposed(Move move) {
        // Save the current state
        Piece piece = move.getPiece();
        Point source = move.getSource();
        Point dest = move.getDestination();
        Piece capturedPiece = board.getPiece(dest);
        boolean isWhite = piece.isWhite();
        
        // Simulate the move
        board.updatePiecePosition(piece, source, dest);
        
        // Check if the king is in check after the move
        boolean kingInCheck = isKingInCheck(isWhite);
        
        // Restore the board state
        board.undoMove(piece, source, dest, capturedPiece);
        
        return kingInCheck;
    }
    
    public boolean isKingInCheck(boolean isWhite) {
        King king = board.findKing(isWhite);
        if (king == null) {
            return false;
        }
        
        // Check if any opponent piece can attack the king
        for (Piece piece : board.getPieces(!isWhite)) {
            if (piece.canMove(king.getX(), king.getY())) {
                return true;
            }
        }
        return false;
    }
    
    // New method to avoid recursion when checking if king is in check
    private boolean isKingInCheckAfterMove(boolean isWhite) {
        King king = board.findKing(isWhite);
        if (king == null) {
            return false;
        }
        
        // Check if any opponent piece can attack the king's position
        for (Piece piece : board.getPieces(!isWhite)) {
            // Skip captured pieces
            if (piece.isCaptured()) {
                DebugUtils.log("Skipping captured piece: " + piece.getClass().getSimpleName());
                continue;
            }
            
            // Debug output the piece we're checking
            boolean canAttack = canPieceAttackSquare(piece, king.getX(), king.getY());
            if (canAttack) {
                DebugUtils.log("King would be attacked by " + piece.getClass().getSimpleName() + 
                                 " at " + piece.getX() + "," + piece.getY());
                return true;
            }
        }
        
        return false;
    }
    
    // Helper method to check if a piece can attack a square without considering check
    private boolean canPieceAttackSquare(Piece piece, int x, int y) {
        // Basic movement checks without recursively checking for check
        if (piece instanceof Pawn) {
            // Pawns capture diagonally
            int direction = piece.isWhite() ? -1 : 1;
            return (Math.abs(piece.getX() - x) == 1 && (y - piece.getY()) == direction);
        }
        
        // For other pieces, just use their normal movement logic
        return piece.canMove(x, y);
    }
    
    public boolean isCheckmate(boolean isWhite) {
        // First, check if the king is in check
        if (!isKingInCheck(isWhite)) {
            return false;
        }
        
        // Then, try all possible moves for all pieces of the player
        for (Piece piece : board.getPieces(isWhite)) {
            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    Point source = new Point(piece.getX(), piece.getY());
                    Point dest = new Point(x, y);
                    Move move = new Move(source, dest, piece, MoveType.NORMAL);
                    
                    if (isValidMove(move)) {
                        return false; // Found a legal move, not checkmate
                    }
                }
            }
        }
        
        // No legal moves, it's checkmate
        return true;
    }
    
    // NEW METHOD: Check if a move would resolve an existing check
    private boolean wouldMoveResolveCheck(Move move) {
        Piece piece = move.getPiece();
        Point source = move.getSource();
        Point dest = move.getDestination();
        
        // Remember the current state
        Piece capturedPiece = board.getPiece(dest);
        Piece enPassantCapturedPiece = null;
        int originalX = piece.getX();
        int originalY = piece.getY();
        
        // Handle en passant capture during simulation
        if (move.getMoveType() == MoveType.EN_PASSANT) {
            enPassantCapturedPiece = board.getPiece(dest.x, source.y);
        }
        
        // Enter simulation mode
        DebugUtils.enterSimulationMode();
        
        // Mark captured pieces as captured during simulation
        if (capturedPiece != null) {
            capturedPiece.setCaptured(true);
        }
        if (enPassantCapturedPiece != null) {
            enPassantCapturedPiece.setCaptured(true);
        }
        
        // Temporarily update board
        board.updatePiecePosition(piece, source, dest);
        piece.setX(dest.x);
        piece.setY(dest.y);
        
        // Simulate en passant capture if applicable
        if (enPassantCapturedPiece != null) {
            board.setSquare(dest.x, source.y, null); // Remove captured pawn
        }
        
        // Check if king is still in check
        boolean stillInCheck = isKingInCheckAfterMove(piece.isWhite());
        
        // Restore the original state
        board.updatePiecePosition(piece, dest, source);
        piece.setX(originalX);
        piece.setY(originalY);
        
        // Restore captured pieces
        if (capturedPiece != null) {
            board.setSquare(dest.x, dest.y, capturedPiece);
            capturedPiece.setCaptured(false);
        }
        if (enPassantCapturedPiece != null) {
            board.setSquare(dest.x, source.y, enPassantCapturedPiece);
            enPassantCapturedPiece.setCaptured(false);
        }
        
        // Exit simulation mode
        DebugUtils.exitSimulationMode();
        
        // Return true only if check is resolved
        DebugUtils.log("Move " + (stillInCheck ? "does not resolve" : "resolves") + " check");
        return !stillInCheck;
    }
}
