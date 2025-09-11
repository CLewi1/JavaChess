package com.clewi.javachess.model;

/**
 * Simple wrapper for displaying move history from saved MoveData
 */
public class DisplayableMove {
    private final String moveText;
    
    public DisplayableMove(MoveData moveData) {
        this.moveText = createMoveText(moveData);
    }
    
    private String createMoveText(MoveData moveData) {
        // Convert coordinates to chess notation
        char sourceFile = (char)('a' + moveData.getSource().x);
        int sourceRank = 8 - moveData.getSource().y;
        char destFile = (char)('a' + moveData.getDestination().x);
        int destRank = 8 - moveData.getDestination().y;
        
        // Get piece initial (first letter of piece type)
        String pieceSymbol = moveData.getPieceType().substring(0, 1);
        
        // Handle special cases
        if (moveData.getMoveType() == MoveType.CASTLE) {
            // Determine if kingside or queenside based on destination
            boolean isKingside = moveData.getDestination().x > moveData.getSource().x;
            return isKingside ? "O-O" : "O-O-O";
        }
        
        // Standard move notation: piece + source + destination
        String move = pieceSymbol + sourceFile + sourceRank + destFile + destRank;
        
        // Add promotion notation if applicable
        if (moveData.getPromotionChoice() != null && !moveData.getPromotionChoice().isEmpty()) {
            move += "=" + moveData.getPromotionChoice().substring(0, 1);
        }
        
        return move;
    }
    
    @Override
    public String toString() {
        return moveText;
    }
}
