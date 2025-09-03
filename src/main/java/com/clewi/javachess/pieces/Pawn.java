package com.clewi.javachess.pieces;

import java.awt.Point;
import com.clewi.javachess.model.Board;
import com.clewi.javachess.util.DebugUtils;

public class Pawn extends Piece {
    private boolean hasMoved = false;

    public Pawn(int x, int y, boolean is_white, String file_path, Board board, boolean is_captured)
    {
        super(x, y, is_white, file_path, board, is_captured);
    }
    
    @Override
    public boolean canMove(int destination_x, int destination_y) {
        // Check if board reference exists
        if (board == null) {
            DebugUtils.log("Board is null in Pawn.canMove");
            return false;
        }
        
        int x = this.getX();
        int y = this.getY();
        
        DebugUtils.logPawnMove("Pawn at " + x + "," + y + " checking if can move to " + 
                          destination_x + "," + destination_y);
        
        if (!isWithinBounds(destination_x, destination_y)) {
            DebugUtils.logPawnMove("Move is out of bounds");
            return false;
        }
        
        // Movement direction depends on the color
        int direction = isWhite() ? -1 : 1;
        
        // Moving straight ahead (no capture)
        if (destination_x == x) {
            // One square forward
            if (destination_y == y + direction) {
                boolean canMove = board.getPiece(destination_x, destination_y) == null;
                DebugUtils.logPawnMove("One square forward: " + canMove);
                return canMove;
            }
            
            // Two squares forward from starting position
            if (!hasMoved && destination_y == y + 2 * direction) {
                boolean pathClear = board.getPiece(x, y + direction) == null;
                boolean destClear = board.getPiece(destination_x, destination_y) == null;
                boolean canMove = pathClear && destClear;
                DebugUtils.logPawnMove("Two squares forward: " + canMove);
                return canMove;
            }
        }
        
        // Diagonal capture
        if (Math.abs(destination_x - x) == 1 && destination_y == y + direction) {
            Piece pieceAtDestination = board.getPiece(destination_x, destination_y);
            boolean canCapture = pieceAtDestination != null && pieceAtDestination.isWhite() != isWhite();
            DebugUtils.logPawnMove("Diagonal capture: " + canCapture);
            return canCapture;
        }
        
        DebugUtils.logPawnMove("Move doesn't match any pawn movement pattern");
        return false;
    }
    
    public void setHasMoved() {
        this.hasMoved = true;
    }
    
    public boolean getHasMoved() {
        return hasMoved;
    }
}
